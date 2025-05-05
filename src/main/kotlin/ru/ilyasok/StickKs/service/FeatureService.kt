package ru.ilyasok.StickKs.service

import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.toList
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.ilyasok.StickKs.model.Feature
import ru.ilyasok.StickKs.repository.IFeatureRepository
import java.util.UUID

@Service
class FeatureService(
    private val compilationService: FeatureCompilationService,
    private val featureRepository: IFeatureRepository,
    private val featureProvider: FeatureProvider
) {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(FeatureService::class.java)
    }

    suspend fun save(id: UUID?, featureCode: String): Feature {
        val compilationResult = compilationService.compile(featureCode)
        val feature = if (compilationResult.success) {
            Feature(id = id ?: UUID.randomUUID(), name = compilationResult.featureBlock?.name, code = featureCode)
        } else {
            throw RuntimeException("failed to save feature: ${compilationResult.error?.message}")
        }

        try {
            featureRepository.save(feature).also {
                logger.info("successfully saved feature with id: ${feature.id}")
            }
        } catch (e: Exception) {
            logger.error("failed to save feature with id: ${feature.id}", e)
            throw RuntimeException("failed to save feature with id:\n ${feature.id}", e)
        }

        featureProvider.provide(compilationResult.featureBlock!!)
        return feature
    }

    suspend fun getAllStable(): List<Feature> {
        return featureRepository.findAll()
            .filter { f -> compilationService.compile(f.code).success }
            .toList()
    }

}