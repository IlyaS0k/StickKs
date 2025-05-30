package ru.ilyasok.StickKs.core.feature

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import ru.ilyasok.StickKs.model.NotificationType
import ru.ilyasok.StickKs.service.FeatureErrorsService

@Component
class DefaultFetchUpdatesStrategy(
    val featureManager: FeatureManager,
    val featureErrorsService: FeatureErrorsService
) : IFetchUpdatesStrategy {

    private val notificationService = featureManager.notificationService
    private val featureService = featureManager.featureService
    private val fetchUpdatesQueue = featureManager.featureUpdatesQueue
    override fun name(): String = "DEFAULT"

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java)
    }

    override suspend fun fetch() {
        fetchUpdatesQueue.get().collect { updateInfo ->
            when (updateInfo.updateType) {
                FeatureUpdateType.CODE_UPDATED -> onCodeUpdated(updateInfo)

                FeatureUpdateType.CREATED -> onCreated(updateInfo)

                FeatureUpdateType.DELETED -> onDeleted(updateInfo)
            }
        }
    }

    private suspend fun notifyOnErrors(featureUpdate: FeatureUpdateInfo, block: suspend () -> Unit) {
        try {
            block()
        } catch (e: Throwable) {
            notificationService.notify(featureUpdate.id, null, NotificationType.FEATURE_UNSTABLE)
            featureErrorsService.updateFeatureErrors(featureUpdate.id, e.stackTraceToString())
        }
    }

    private suspend fun onCreated(updateInfo: FeatureUpdateInfo) {
        val created = featureService.getByIdCompiled(updateInfo.id)
        notifyOnErrors(updateInfo) {
            featureManager.applyNew(created)
            notificationService.notify(updateInfo.id, updateInfo.reqId, NotificationType.FEATURE_CREATED)
        }
    }

    private suspend fun onCodeUpdated(updateInfo: FeatureUpdateInfo) {
        notifyOnErrors(updateInfo) {
            val updated = featureService.getByIdCompiled(updateInfo.id)
            featureManager.apply(updated)
            notificationService.notify(updateInfo.id, updateInfo.reqId, NotificationType.FEATURE_UPDATED)
        }
    }

    private suspend fun onDeleted(updateInfo: FeatureUpdateInfo) {
        val exists = featureService.existsById(updateInfo.id)
        if (!exists) {
            notifyOnErrors(updateInfo) {
                featureManager.delete(updateInfo.id)
            }
        }
        notificationService.notify(updateInfo.id, updateInfo.reqId, NotificationType.FEATURE_DELETED)
    }
}
