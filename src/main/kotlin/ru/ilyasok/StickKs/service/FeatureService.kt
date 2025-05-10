package ru.ilyasok.StickKs.service

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.toList
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.DependsOn
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.ilyasok.StickKs.core.FeatureUpdateType
import ru.ilyasok.StickKs.core.FeaturesToUpdate
import ru.ilyasok.StickKs.dsl.Feature
import ru.ilyasok.StickKs.dsl.FeatureBlock
import ru.ilyasok.StickKs.dsl.FeatureMeta
import ru.ilyasok.StickKs.model.FeatureModel
import ru.ilyasok.StickKs.model.FeatureStability
import ru.ilyasok.StickKs.model.toFeature
import ru.ilyasok.StickKs.repository.IFeatureRepository
import java.io.File
import java.time.Instant
import java.util.UUID


@Service
@DependsOn("flyway")
class FeatureService(
    private val compilationService: FeatureCompilationService,
    private val featureRepository: IFeatureRepository,
    private val featuresToUpdate: FeaturesToUpdate,
) {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(FeatureService::class.java)
    }

    private suspend fun <T> optimisticTry(maxAttempts: Long = Long.MAX_VALUE, block: suspend () -> T): T {
        val attempts = 0
        while (attempts < maxAttempts) {
            try {
                return block()
            } catch (_: OptimisticLockingFailureException) {
                attempts.inc()
            }
        }
        throw RuntimeException("Max optimistic retry attempts exceeded")
    }

    @Transactional
    suspend fun save(id: UUID?, featureCode: String): FeatureModel {
        val isUpdate = id != null
        val compilationResult = compilationService.compile(featureCode)
        if (!compilationResult.success) {
            throw RuntimeException("Failed to save feature: ${compilationResult.error?.toString()}")
        }
        return if (isUpdate)
            update(id, featureCode, compilationResult.featureBlock!!)
        else
            create(featureCode, compilationResult.featureBlock!!)
    }

    @Transactional
    suspend fun delete(id: UUID) {
        if (featureRepository.existsById(id)) featureRepository.deleteById(id)
        featuresToUpdate.add(id, FeatureUpdateType.DELETED)
    }


    private suspend fun update(id: UUID, featureCode: String, compiledFeature: FeatureBlock): FeatureModel {
        val featureModel = featureRepository.findById(id)
        requireNotNull(featureModel)

        val updatedFeatureModel = try {
            optimisticTry {
                featureRepository.save(
                    featureModel.copy(
                        name = compiledFeature.name,
                        code = featureCode,
                        lastModifiedAt = Instant.now()
                    )
                ).also {
                    logger.info("Successfully update feature with id: $id")
                }
            }
        } catch (e: Throwable) {
            val message = "Failed to update feature with id: $id"
            logger.error(message, e)
            throw RuntimeException(message, e)
        }
        featuresToUpdate.add(id, FeatureUpdateType.CODE_UPDATED)

        return updatedFeatureModel
    }

    private suspend fun create(featureCode: String, compiledFeature: FeatureBlock): FeatureModel {
        val id = UUID.randomUUID()
        val createdFeatureModel = try {
            optimisticTry {
                featureRepository.save(
                    FeatureModel(
                        id = id,
                        name = compiledFeature.name,
                        code = featureCode,
                        createdAt = Instant.now(),
                        lastModifiedAt = Instant.now(),
                        successExecutionsAmount = 0L,
                        failedExecutionsAmount = 0L,
                    )
                ).also {
                    logger.info("Successfully created feature with id: $id")
                }
            }
        } catch (e: Throwable) {
            val message = "Failed to create feature with id: $id"
            logger.error(message, e)
            throw RuntimeException(message, e)
        }
        featuresToUpdate.add(id, FeatureUpdateType.CREATED)

        return createdFeatureModel
    }

    @Transactional
    suspend fun updateMeta(id: UUID, newMeta: FeatureMeta): FeatureModel {
        val feature = featureRepository.findById(id)
        requireNotNull(feature)
        val compilationResult = compilationService.compile(feature.code)
        if (!compilationResult.success) {
            throw RuntimeException("Failed to compile feature with id: $id")
        }

        val updatedFeatureModel = try {
            optimisticTry {
                featureRepository.save(
                    feature.copy(
                        createdAt = newMeta.createdAt,
                        lastModifiedAt = newMeta.lastModifiedAt,
                        lastSuccessExecutionAt = newMeta.lastSuccessExecutionAt,
                        lastFailedExecutionAt = newMeta.lastFailedExecutionAt,
                        successExecutionsAmount = newMeta.successExecutionsAmount,
                        failedExecutionsAmount = newMeta.failedExecutionsAmount
                    )
                ).also {
                    logger.info("Successfully update meta about feature with id: $id")
                }
            }
        } catch (e: Throwable) {
            val message = "Successfully update meta about feature with id: $id"
            logger.error(message, e)
            throw RuntimeException(message, e)
        }
        featuresToUpdate.add(id, FeatureUpdateType.META_UPDATED)

        return updatedFeatureModel
    }

    suspend fun getMeta(id: UUID): FeatureMeta {
        val feature = featureRepository.findById(id) ?: throw RuntimeException("Feature not found with id: $id")

        return FeatureMeta(
            createdAt = feature.createdAt,
            lastModifiedAt = feature.lastModifiedAt,
            lastSuccessExecutionAt = feature.lastSuccessExecutionAt,
            lastFailedExecutionAt = feature.lastFailedExecutionAt,
            successExecutionsAmount = feature.successExecutionsAmount,
            failedExecutionsAmount = feature.failedExecutionsAmount
        )
    }

    suspend fun getById(id: UUID): Feature {
        val feature = featureRepository.findById(id) ?: throw RuntimeException("Feature not found with id: $id")

        return mapFeatureModelToFeatureOrNull(feature) ?: throw RuntimeException("Failed to compile feature")
    }

    suspend fun existsById(id: UUID): Boolean {
        return featureRepository.existsById(id)
    }

    fun getAll(): Flow<FeatureModel> {
        return featureRepository.findAll()
            .map { feature ->
                val featureCompileJob = compilationService.compileAsync(feature.code)
                feature to featureCompileJob
            }
            .buffer()
            .map { (feature, featureCompileJob) ->
                val compilationResult = featureCompileJob.await()
                feature.stability = if (compilationResult.success)
                    FeatureStability.STABLE
                else
                    FeatureStability.BROKEN
                feature
            }
    }

    fun getAllStable(): Flow<Feature> {
        return featureRepository.findAll().mapNotNull { f -> mapFeatureModelToFeatureOrNull(f) }
    }

    private fun mapFeatureModelToFeatureOrNull(featureModel: FeatureModel): Feature? {
        val compilation = compilationService.compile(featureModel.code)
        return if (compilation.success) {
            featureModel.toFeature(compilation.featureBlock!!)
        } else {
            null
        }
    }

}