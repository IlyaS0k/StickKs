package ru.ilyasok.StickKs.client

import kotlinx.coroutines.delay
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service
import ru.ilyasok.StickKs.controller.FeatureController
import ru.ilyasok.StickKs.model.FeatureModel
import ru.ilyasok.StickKs.model.NotificationType
import ru.ilyasok.StickKs.model.SaveFeatureRequest
import java.util.*
import kotlin.time.Duration.Companion.seconds

@Service
@Lazy
class FeatureClient(
    private val controller: FeatureController,
    private val wsWrapper: WsConnectionWrapper
) {
    suspend fun createFeature(rq: SaveFeatureRequest): FeatureModel {
        return controller.save(UUID.randomUUID(), rq)
    }

    suspend fun createFeatureAndWaitApply(rq: SaveFeatureRequest): FeatureModel {
        val feature = createFeature(rq)
        delay(1.seconds)
        val receivedEvents = wsWrapper.getAllReceivedEvents()
        if (receivedEvents.none { it.type == NotificationType.FEATURE_CREATED && it.id == feature.id }) {
            throw IllegalStateException("Failed to apply feature")
        }
        return feature
    }
}