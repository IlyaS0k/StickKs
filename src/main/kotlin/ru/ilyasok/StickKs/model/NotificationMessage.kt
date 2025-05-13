package ru.ilyasok.StickKs.model

import java.util.UUID

data class NotificationMessage(
    val reqId: UUID? = null,
    val id: UUID,
    val type: NotificationType
)

enum class NotificationType {
    FEATURE_LOADED,
    FEATURE_DELETED,
    FEATURE_CREATED,
    FEATURE_UPDATED,
    FEATURE_UNSTABLE
}