package ru.ilyasok.StickKs.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.ilyasok.StickKs.model.Feature
import ru.ilyasok.StickKs.repository.IFeatureRepository
import java.util.UUID

@Service
class FeatureService(
    private val compilationService: FeatureCompilationService,
    private val featureRepository: IFeatureRepository
) {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(FeatureService::class.java)
    }

    fun save(id: UUID?, featureCode: String): Feature {
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

        return feature
    }

    fun getAllStable(): List<Feature> {
        val features = featureRepository.findAll()
        return features.filter { f ->
            try {
                compilationService.compile(f.code).takeIf { it.success }!!.featureBlock
                true
            } catch (_ : Throwable) {
                false
            }
        }
    }


}