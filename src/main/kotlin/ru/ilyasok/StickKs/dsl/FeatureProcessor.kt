package ru.ilyasok.StickKs.dsl

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import ru.ilyasok.StickKs.core.feature.FeatureManager
import ru.ilyasok.StickKs.core.context.EventContext
import ru.ilyasok.StickKs.model.FeatureStatus
import ru.ilyasok.StickKs.model.NotificationType
import ru.ilyasok.StickKs.service.FeatureErrorsService
import ru.ilyasok.StickKs.service.FeatureService
import ru.ilyasok.StickKs.service.NotificationService
import java.time.Instant

@Component
class FeatureProcessor(
    private val featureManager: FeatureManager,
    private val featureService: FeatureService,
    private val featureErrorsService: FeatureErrorsService,
    private val notificationService: NotificationService
) {

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java)
    }

    suspend fun process(eventContext: EventContext) {
        featureManager.getFeatures().forEach { feature ->
            var updatedMeta: FeatureMeta? = null
            try {
                feature
                    .takeIf { feature -> feature.feature.onEvent.event.contextType == eventContext::class }
                    ?.takeIf { feature -> feature.feature.onEvent.event.checkCondition(eventContext) }
                    ?.let { feature ->
                        logger.info("start process feature $feature")
                        feature.feature.onEvent.event.execute(eventContext)
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
                    featureService.updateMeta(feature.id, updatedMeta!!)
                }
            }
        }
    }
}