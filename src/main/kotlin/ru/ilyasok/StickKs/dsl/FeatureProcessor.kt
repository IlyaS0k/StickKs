package ru.ilyasok.StickKs.dsl

import org.springframework.stereotype.Component
import ru.ilyasok.StickKs.core.FeatureManager
import ru.ilyasok.StickKs.core.context.EventContext

@Component
open class FeatureProcessor(
    private val featureManager: FeatureManager
) {

    suspend fun process(eventContext: EventContext) {
        featureManager.getFeatures()
            .flatMap { feature -> feature.onEvent.events.filter { event -> event.contextType == eventContext::class } }
            .forEach { event -> event.execute(eventContext) }
    }
}