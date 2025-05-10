package ru.ilyasok.StickKs.dsl

import java.time.Instant
import java.util.UUID

data class Feature(
    val id: UUID,
    val version: Long,
    val feature: FeatureBlock,
    val meta: FeatureMeta
) {
    fun isAvailable() = feature.availability.isAvailable(meta)
}

data class FeatureMeta(
    val createdAt: Instant,
    val lastModifiedAt: Instant,
    val lastSuccessExecutionAt: Instant?,
    val lastFailedExecutionAt: Instant?,
    val successExecutionsAmount: Long,
    val failedExecutionsAmount: Long
)