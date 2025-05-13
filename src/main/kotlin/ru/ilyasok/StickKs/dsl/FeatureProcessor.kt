package ru.ilyasok.StickKs.dsl

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import ru.ilyasok.StickKs.core.feature.FeatureManager
import ru.ilyasok.StickKs.core.context.EventContext
import ru.ilyasok.StickKs.model.NotificationType
import ru.ilyasok.StickKs.service.FeatureService
import ru.ilyasok.StickKs.service.NotificationService
import java.time.Instant

@Component
class FeatureProcessor(
    private val featureManager: FeatureManager,
    private val featureService: FeatureService,
    private val notificationService: NotificationService
) {

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java)
    }

    suspend fun process(eventContext: EventContext) {
        featureManager.getFeatures()
            .filter { feature -> feature.feature.onEvent.event.contextType == eventContext::class }
            .forEach { feature ->
                var updatedMeta :FeatureMeta? = null
                try {
                    logger.info("start process feature $feature")
                    feature.feature.onEvent.event.execute(eventContext)
                    updatedMeta = feature.meta.copy(lastSuccessExecutionAt = Instant.now())
                    logger.info("successfully process feature $feature")
                } catch (_: Throwable) {
                    logger.info("failed to process feature $feature")
                    updatedMeta = feature.meta.copy(lastFailedExecutionAt = Instant.now())
                    notificationService.notify(feature.id, null, NotificationType.FEATURE_UNSTABLE)
                } finally {
                    featureService.updateMeta(feature.id, updatedMeta!!)
                }
            }
    }
}