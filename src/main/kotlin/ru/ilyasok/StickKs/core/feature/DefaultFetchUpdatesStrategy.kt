package ru.ilyasok.StickKs.core.feature

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import ru.ilyasok.StickKs.dsl.Feature
import ru.ilyasok.StickKs.model.NotificationType
import ru.ilyasok.StickKs.service.FeatureService
import ru.ilyasok.StickKs.service.NotificationService

class DefaultFetchUpdatesStrategy(
    private val featureUpdatesQueue: FeatureUpdatesQueue,
    private val featuresMutex: Mutex,
    private val features: MutableList<Feature>,
    private val featureService: FeatureService,
    private val notificationService: NotificationService
) : IFetchUpdatesStrategy {

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java)
    }

    override suspend fun fetch() {
        featureUpdatesQueue.get().collect { updateInfo ->
            when (updateInfo.updateType) {
                FeatureUpdateType.CODE_UPDATED -> {
                    val updated = featureService.getById(updateInfo.id)
                    featuresMutex.withLock {
                        val index = features.indexOfFirst { it.id == updateInfo.id }
                        if (index != -1) features[index] = updated else features.add(updated)
                    }
                    notificationService.notify(updateInfo.id, updateInfo.reqId, NotificationType.FEATURE_UPDATED)
                }

                FeatureUpdateType.CREATED -> {
                    val created = featureService.getById(updateInfo.id)
                    featuresMutex.withLock {
                        features.add(created)
                    }
                    notificationService.notify(updateInfo.id, updateInfo.reqId, NotificationType.FEATURE_CREATED)
                }

                FeatureUpdateType.DELETED -> {
                    val exists = featureService.existsById(updateInfo.id)
                    if (!exists) {
                        featuresMutex.withLock {
                            features.removeAll { it.id == updateInfo.id }
                        }
                    }
                    notificationService.notify(updateInfo.id, updateInfo.reqId, NotificationType.FEATURE_DELETED)
                }
            }
        }
    }
}