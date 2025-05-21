package ru.ilyasok.StickKs.dsl

import jakarta.annotation.PostConstruct
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import ru.ilyasok.StickKs.core.feature.FeatureManager
import ru.ilyasok.StickKs.core.context.EventContext
import ru.ilyasok.StickKs.core.event.TimerEvent
import ru.ilyasok.StickKs.core.event.queue.EventQueue
import ru.ilyasok.StickKs.model.FeatureStatus
import ru.ilyasok.StickKs.model.NotificationType
import ru.ilyasok.StickKs.service.FeatureErrorsService
import ru.ilyasok.StickKs.service.FeatureService
import ru.ilyasok.StickKs.service.NotificationService
import java.time.Instant
import kotlin.coroutines.cancellation.CancellationException

@Component
class FeatureProcessor(
    private val featureManager: FeatureManager,
    private val featureService: FeatureService,
    private val featureErrorsService: FeatureErrorsService,
    private val notificationService: NotificationService,
    private val eventQueue: EventQueue
) {

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java)
    }

    private lateinit var loopJob: Job

    @PostConstruct
    fun postConstruct() = runBlocking {
        startLoop()
    }

    fun startLoop() {
        if (::loopJob.isInitialized) {
            loopJob.cancel()
        }
        loopJob = CoroutineScope(Dispatchers.Default + CoroutineName("EventQueueCoro")).launch {
            loop()
        }
    }

    private suspend fun loop() = coroutineScope {
        while (true) {
            try {
                val ec = eventQueue.dequeue()
                logger.info("received event ${ec.hashCode()}")
                process(ec)
            } catch (_: CancellationException) {
                logger.info("EventQueue loop cancelled")
                break
            } catch (e: Throwable) {
                logger.warn("Exception in EventQueue loop: ", e)
            }
        }
    }

    suspend fun process(eventContext: EventContext) {
        featureManager.getFeatures().forEach { feature ->
            var updatedMeta: FeatureMeta? = null
            try {
                feature
                    .takeIf { feature -> feature.checkEvent(eventContext) }
                    ?.takeIf { feature -> feature.checkCondition(eventContext) }
                    ?.let { feature ->
                        logger.info("start process feature $feature")
                        feature.execute(eventContext)
                        updatedMeta = feature.meta.copy(
                            lastSuccessExecutionAt = Instant.now(),
                            successExecutionsAmount = feature.meta.successExecutionsAmount + 1
                        )
                        logger.info("successfully process feature $feature")
                    }
            } catch (e: Throwable) {
                logger.warn("failed to process feature $feature", e)
                updatedMeta = feature.meta.copy(
                    status = FeatureStatus.UNSTABLE,
                    lastFailedExecutionAt = Instant.now(),
                    failedExecutionsAmount = feature.meta.failedExecutionsAmount + 1
                )
                featureErrorsService.updateFeatureErrors(feature.id, e.stackTraceToString())
                notificationService.notify(feature.id, null, NotificationType.FEATURE_UNSTABLE)
            } finally {
                if (updatedMeta != null) {
                    logger.debug("start updating feature({}) meta after processing", feature.id)
                    featureService.updateMeta(feature.id, updatedMeta!!)
                }
            }
        }
    }

    private fun <T : EventContext> Feature.checkEvent(eventContext: T): Boolean {
        val onEvent = this.feature.onEvent ?: return eventContext is TimerEvent && eventContext.featureId == this.id
        return onEvent.event.contextType == eventContext::class
    }

    private suspend fun <T : EventContext> Feature.checkCondition(eventContext: T): Boolean {
        val onEvent = this.feature.onEvent ?: return this.feature.runBlock!!.checkCondition()
        return onEvent.event.checkCondition(eventContext)
    }

    private suspend fun <T : EventContext> Feature.execute(eventContext: T) {
        val onEvent = this.feature.onEvent ?: return this.feature.runBlock!!.execute()
        return onEvent.event.execute(eventContext)
    }
}