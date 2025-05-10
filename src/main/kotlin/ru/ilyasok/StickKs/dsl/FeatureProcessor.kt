package ru.ilyasok.StickKs.dsl

import org.springframework.stereotype.Component
import ru.ilyasok.StickKs.core.FeatureCollection
import ru.ilyasok.StickKs.core.context.EventContext
import ru.ilyasok.StickKs.service.FeatureService
import java.time.Instant

@Component
class FeatureProcessor(
    private val featureCollection: FeatureCollection,
    private val featureService: FeatureService
) {

    suspend fun process(eventContext: EventContext) {
        featureCollection.getFeatures()
            .filter { feature -> feature.feature.onEvent.event.contextType == eventContext::class }
            .forEach { feature ->
                val updatedMeta = try {
                    feature.feature.onEvent.event.execute(eventContext)
                    feature.meta.copy(lastSuccessExecutionAt = Instant.now())
                } catch (_: Throwable) {
                    feature.meta.copy(lastFailedExecutionAt = Instant.now())
                }
                featureService.updateMeta(feature.id, updatedMeta)
            }
    }
}