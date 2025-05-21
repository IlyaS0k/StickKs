package ru.ilyasok.StickKs.dsl

import ru.ilyasok.StickKs.model.FeatureStatus
import java.time.Instant
import java.util.UUID

data class Feature(
    val id: UUID,
    val version: Long,
    val feature: FeatureBlock,
    val meta: FeatureMeta
) {
    fun control() = feature.executionControl.control(meta)

    fun isEnabled() = !meta.disabled
}

data class FeatureMeta(
    val status: FeatureStatus,
    val disabled: Boolean,
    val createdAt: Instant,
    val lastModifiedAt: Instant,
    val lastSuccessExecutionAt: Instant?,
    val lastFailedExecutionAt: Instant?,
    val successExecutionsAmount: Long,
    val failedExecutionsAmount: Long
)