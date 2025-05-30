package ru.ilyasok.StickKs.dsl

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import ru.ilyasok.StickKs.model.FeatureStatus
import java.time.Instant
import java.util.UUID

data class Feature(
    val id: UUID,
    val name: String,
    val version: Long,
    val feature: FeatureBlock,
    val meta: FeatureMeta,
    private val processingMutex: Mutex = Mutex(),
) {

    suspend fun process(block: suspend (Feature) -> Unit) = processingMutex.withLock { block(this) }

    fun control() = if (!isActivated()) feature.executionControl.control(meta) else true

    fun isEnabled() = !meta.disabled

    fun idName() = "[$name| $id]"

    fun isActivated() = feature.activated
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