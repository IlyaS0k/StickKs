package ru.ilyasok.StickKs.core

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class FeaturesToUpdate {
    private val featureIds: MutableList<FeatureUpdateInfo> = mutableListOf()

    private val mutex = Mutex()

    suspend fun add(id: UUID, updateType: FeatureUpdateType) = mutex.withLock {
        featureIds.add(FeatureUpdateInfo(id, updateType))
    }

    suspend fun get(): List<FeatureUpdateInfo> = mutex.withLock {
        val result = featureIds.toList()
        featureIds.clear()

        return@withLock result
    }
}

data class FeatureUpdateInfo(
    val id : UUID,
    val updateType: FeatureUpdateType
)

enum class FeatureUpdateType {
    CREATED,
    DELETED,
    CODE_UPDATED,
    META_UPDATED
}