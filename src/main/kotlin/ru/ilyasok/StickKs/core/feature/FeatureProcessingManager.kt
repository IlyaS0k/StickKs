package ru.ilyasok.StickKs.core.feature

import jakarta.annotation.PostConstruct
import kotlinx.coroutines.Job
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import ru.ilyasok.StickKs.core.event.queue.EventQueue
import ru.ilyasok.StickKs.dsl.FeatureProcessor
import kotlin.time.Duration.Companion.minutes

@Component
class FeatureProcessingManager(
    private val featureManager: FeatureManager,
    private val featureUpdatesQueue: FeatureUpdatesQueue,
    private val featureProcessor: FeatureProcessor,
) {

    private var disableUpdatesQueueJob: Job? = null

    private val mutex = Mutex()

    companion object {
        private val DISABLE_UPDATES_QUEUE_TIMEOUT = 5.minutes

        private val logger = LoggerFactory.getLogger(this::class.java)
    }

    @PostConstruct
    fun postConstruct() = runBlocking {
        enable()
    }

    suspend fun enable(refresh: Boolean = false) = mutex.withLock {
        try {
            if (refresh) featureProcessor.startLoop()
            featureUpdatesQueue.enable()
            featureManager.enable(refresh)
            logger.info("Feature processing enabled")
        } catch (e: Throwable) {
            logger.info("Failed to enable feature processing", e)
        }
    }

    suspend fun disable() = mutex.withLock {
        try {
            if (disableUpdatesQueueJob == null || disableUpdatesQueueJob?.isActive == false) {
                disableUpdatesQueueJob = featureUpdatesQueue.disableAfterTimeout(DISABLE_UPDATES_QUEUE_TIMEOUT)
            }
            featureManager.disable()
            logger.info("Feature processing disabled")
        } catch (e: Throwable) {
            logger.info("Failed to disable feature processing", e)
        }
    }
}