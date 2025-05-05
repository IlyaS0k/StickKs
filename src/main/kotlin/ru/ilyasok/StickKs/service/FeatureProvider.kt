package ru.ilyasok.StickKs.service

import jakarta.annotation.PostConstruct
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Service
import ru.ilyasok.StickKs.core.FeatureManager
import ru.ilyasok.StickKs.dsl.FeatureBlock
import ru.ilyasok.StickKs.repository.IFeatureRepository

@Service
class FeatureProvider (
    private val compilationService: FeatureCompilationService,
    private val featureRepository: IFeatureRepository,
    private val featureManager: FeatureManager
) {

    @PostConstruct
    fun postConstruct() {
        runBlocking {
            val features = featureRepository.findAll()
                .mapNotNull { f -> compilationService.compile(f.code).takeIf { it.success }?.featureBlock }
                .toList()
            featureManager.addAll(features)
        }
    }

    fun provide(feature: FeatureBlock) {
        featureManager.add(feature)
    }
}