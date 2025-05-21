package ru.ilyasok.StickKs.core.feature

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.springframework.stereotype.Component
import ru.ilyasok.StickKs.dsl.Feature
import ru.ilyasok.StickKs.model.AvailabilityStatus
import ru.ilyasok.StickKs.model.NotificationType
import ru.ilyasok.StickKs.service.FeatureService
import ru.ilyasok.StickKs.service.NotificationService
import java.util.concurrent.atomic.AtomicReference

@Component
class FeatureManager(
    private val featureService: FeatureService,
    private val featureUpdatesQueue: FeatureUpdatesQueue,
    private val notificationService: NotificationService
) {

    private var features: MutableList<Feature> = mutableListOf()
    private val mutex = Mutex()
    private val status = AtomicReference(AvailabilityStatus.DISABLED)
    private val statusChangedSignal = CompletableDeferred<Unit>()
    private var fetchUpdatesJob: Job? = null
    private val fetchJobMutex = Mutex()

    fun disable() {
        if (status.get() == AvailabilityStatus.DISABLED) return
        status.set(AvailabilityStatus.DISABLED)
        statusChangedSignal.complete(Unit)
        runBlocking {
            fetchJobMutex.withLock {
                fetchUpdatesJob?.cancel()
            }
            mutex.withLock { features.clear() }
        }
    }

    fun enable(refresh: Boolean = false) {
        if (status.get() == AvailabilityStatus.ENABLED && !refresh) return
        CoroutineScope(Dispatchers.Default).launch {
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
            return features.filter { it.isEnabled() && it.control() }
        }
    }

    private suspend fun waitWhileDisabled() {
        while (status.get() == AvailabilityStatus.DISABLED) {
            statusChangedSignal.await()
        }
    }
}