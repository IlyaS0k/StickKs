package ru.ilyasok.StickKs.core

import jakarta.annotation.PostConstruct
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.springframework.stereotype.Component
import ru.ilyasok.StickKs.dsl.Feature
import ru.ilyasok.StickKs.service.FeatureService

@Component
class FeatureCollection(
    private val featureService: FeatureService,
    private val featuresToUpdate: FeaturesToUpdate
) {

    private var features: MutableList<Feature> = mutableListOf()
    private val mutex = Mutex()

    companion object {
        private const val FETCH_PERIOD_MILLIS = 5 * 1000L
    }

    @PostConstruct
    fun onReady() = runBlocking {
        val stableFeatures = featureService.getAllStable().toList()
        mutex.lock { features.addAll(stableFeatures) }
        CoroutineScope(Dispatchers.Default).launch {
            fetchLoop()
        }
    }



    private suspend fun fetchLoop() = coroutineScope {
        launch {
            while (true) {
                delay(FETCH_PERIOD_MILLIS)
                val toUpdate = featuresToUpdate.get()
                if (toUpdate.isNotEmpty()) {
                    mutex.withLock {
                        for (info in toUpdate) {
                            when (info.updateType) {
                                FeatureUpdateType.META_UPDATED -> {
                                    val meta = featureService.getMeta(info.id)
                                    val index = features.indexOfFirst { it.id == info.id }
                                    features[index] = features[index].copy(meta = meta)
                                }

                                FeatureUpdateType.CODE_UPDATED -> {
                                    val updated = featureService.getById(info.id)
                                    val index = features.indexOfFirst { it.id == info.id }
                                    features[index] = updated
                                }

                                FeatureUpdateType.CREATED -> {
                                    val created = featureService.getById(info.id)
                                    features.add(created)
                                }

                                FeatureUpdateType.DELETED -> {
                                    val exists = featureService.existsById(info.id)
                                    if (!exists) {
                                        features.removeAll { it.id == info.id }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    suspend fun getFeatures(): List<Feature> = mutex.withLock {
        return features.toList()
    }
}