package ru.ilyasok.StickKs.core.feature

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.springframework.stereotype.Component
import ru.ilyasok.StickKs.dsl.Feature
import ru.ilyasok.StickKs.dsl.feature
import ru.ilyasok.StickKs.model.AvailabilityStatus
import ru.ilyasok.StickKs.model.NotificationType
import ru.ilyasok.StickKs.service.FeatureService
import ru.ilyasok.StickKs.service.NotificationService
import ru.ilyasok.StickKs.ws.WebSocketSessionManager
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference
import kotlin.time.Duration.Companion.minutes

@Component
class FeatureManager(
    private val featureService: FeatureService,
    private val featureUpdatesQueue: FeatureUpdatesQueue,
    private val sessionManager: WebSocketSessionManager,
    private val notificationService: NotificationService
) {

    private var features: MutableList<Feature> = mutableListOf()
    private val mutex = Mutex()
    private val status = AtomicReference(AvailabilityStatus.DISABLED)
    private val statusChangedSignal = CompletableDeferred<Unit>()
    private var fetchUpdatesJob: Job? = null
    private val fetchJobMutex = Mutex()
    private var disableUpdatesQueueJob: Job? = null
    private val disableUpdatesQueueMutex = Mutex()
    private val firstEnable = AtomicBoolean(true)

    companion object {
        private val CHECK_SESSION_IS_OPEN_PERIOD = 5.minutes
        private val MAX_SESSION_INACTIVE_TIME = 5.minutes
        private val DISABLE_UPDATES_QUEUE_TIMEOUT = 5.minutes
    }

    fun disable() {
        status.set(AvailabilityStatus.DISABLED)
        statusChangedSignal.complete(Unit)
        runBlocking {
            fetchJobMutex.withLock {
                fetchUpdatesJob?.cancel()
            }
            disableUpdatesQueueMutex.withLock {
                if (disableUpdatesQueueJob == null || disableUpdatesQueueJob?.isActive == false) {
                    disableUpdatesQueueJob = featureUpdatesQueue.disableAfterTimeout(DISABLE_UPDATES_QUEUE_TIMEOUT)
                }
            }
            mutex.withLock { features.clear() }
        }
    }

    fun enable() {
        CoroutineScope(Dispatchers.Default).launch {
            if (firstEnable.compareAndSet(true, false)) {
                launch(Dispatchers.IO + CoroutineName("WsSessionActivityCheckCoro")) {
                    wsSessionActivityLoop()
                }
            }
            featureUpdatesQueue.enable()
            mutex.withLock {
                features.clear()
                featureService.getAllCompiled().collect {
                    notificationService.notify(it.id, null, NotificationType.FEATURE_LOADED)
                    features.add(it)
                }
            }
            fetchJobMutex.withLock {
                if (fetchUpdatesJob == null || fetchUpdatesJob?.isActive == false) {
                    fetchUpdatesJob = launch(CoroutineName("FetchUpdatesCoro")) {
                        DefaultFetchUpdatesStrategy(
                            featureUpdatesQueue = featureUpdatesQueue,
                            featuresMutex = mutex,
                            features = features,
                            featureService = featureService,
                            notificationService = notificationService
                        ).loop()
                    }
                }
            }
        }
        status.set(AvailabilityStatus.ENABLED)
        statusChangedSignal.complete(Unit)
    }

    suspend fun getFeatures(): List<Feature> {
        waitWhileDisabled()
        mutex.withLock {
            featureService.getMetaForAll(features.map { it.id }).collect { (id, meta) ->
                val index = features.indexOfFirst { it.id == id }
                assert(index >= 0) { "Illegal update meta result" }
                features[index] = features[index].copy(meta = meta)
            }
            return features.filter { it.isAvailable() }
        }
    }

    private suspend fun waitWhileDisabled() {
        while (status.get() == AvailabilityStatus.DISABLED) {
            statusChangedSignal.await()
        }
    }

    private suspend fun wsSessionActivityLoop() = coroutineScope {
        while (true) {
            try {
                delay(CHECK_SESSION_IS_OPEN_PERIOD)
                if (sessionManager.isInactiveFor(MAX_SESSION_INACTIVE_TIME)) {
                    disable()
                }
            } catch (_: Throwable) {
            }
        }
    }
}