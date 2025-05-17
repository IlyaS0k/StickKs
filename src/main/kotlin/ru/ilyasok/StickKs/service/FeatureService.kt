package ru.ilyasok.StickKs.service

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.DependsOn
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.ilyasok.StickKs.core.feature.FeatureUpdateType
import ru.ilyasok.StickKs.core.feature.FeatureUpdatesQueue
import ru.ilyasok.StickKs.dsl.Feature
import ru.ilyasok.StickKs.dsl.FeatureBlock
import ru.ilyasok.StickKs.dsl.FeatureMeta
import ru.ilyasok.StickKs.model.FeatureModel
import ru.ilyasok.StickKs.model.FeatureStatus
import ru.ilyasok.StickKs.model.toFeature
import ru.ilyasok.StickKs.repository.IFeatureRepository
import java.time.Instant
import java.util.UUID


@Service
@DependsOn("flyway")
class FeatureService(
    private val compilationService: FeatureCompilationService,
    private val featureRepository: IFeatureRepository,
    private val featureUpdatesQueue: FeatureUpdatesQueue
) {

    @Autowired
    @Lazy
    lateinit var self: FeatureService

    companion object {
        val logger: Logger = LoggerFactory.getLogger(FeatureService::class.java)
    }

    suspend fun save(id: UUID?, reqId: UUID, featureCode: String): FeatureModel {
        val isUpdate = id != null
        val compilationResult = compilationService.compile(id, featureCode)
        if (!compilationResult.success) {
            throw RuntimeException("Failed to save feature: ${compilationResult.error?.toString()}")
        }
        return if (isUpdate)
            self.update(id, featureCode, compilationResult.featureBlock!!).also { f ->
                featureUpdatesQueue.add(f.id, reqId, FeatureUpdateType.CODE_UPDATED)
            }
        else
            self.create(featureCode, compilationResult.featureBlock!!).also { f ->
                featureUpdatesQueue.add(f.id, reqId, FeatureUpdateType.CREATED)
            }
    }

    @Transactional
    suspend fun delete(id: UUID, reqId: UUID) {
        if (featureRepository.existsById(id)) featureRepository.deleteById(id)
        featureUpdatesQueue.add(id, reqId, FeatureUpdateType.DELETED)
    }


    @Transactional
    suspend fun update(id: UUID, featureCode: String, compiledFeature: FeatureBlock): FeatureModel {
        val featureModel = featureRepository.findById(id)
        requireNotNull(featureModel)

        val updatedFeatureModel = try {
            optimisticTry(10) {
                featureRepository.save(
                    featureModel.copy(
                        name = compiledFeature.name,
                        code = featureCode,
                        createdAt = Instant.now(),
                        lastModifiedAt = Instant.now(),
                        lastFailedExecutionAt = null,
                        lastSuccessExecutionAt = null,
                        successExecutionsAmount = 0L,
                        failedExecutionsAmount = 0L
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
        updatedFeatureModel.status = FeatureStatus.UPDATING

        return updatedFeatureModel
    }

    @Transactional
    suspend fun create(featureCode: String, compiledFeature: FeatureBlock): FeatureModel {
        val id = UUID.randomUUID()
        val createdFeatureModel = try {
            optimisticTry(10) {
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
        createdFeatureModel.status = FeatureStatus.CREATING

        return createdFeatureModel
    }

    @Transactional
    suspend fun updateMeta(id: UUID, newMeta: FeatureMeta): FeatureModel {
        val feature = featureRepository.findById(id)
        requireNotNull(feature)
        val compilationResult = compilationService.compile(id, feature.code)
        if (!compilationResult.success) {
            throw RuntimeException("Failed to compile feature with id: $id")
        }

        val updatedFeatureModel = try {
            optimisticTry(10) {
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

    suspend fun getMetaForAll(ids: List<UUID>): Flow<Pair<UUID, FeatureMeta>> {
        val features = featureRepository.findAllById(ids)
        val meta = features.map { feature ->
            feature.id to FeatureMeta(
                createdAt = feature.createdAt,
                lastModifiedAt = feature.lastModifiedAt,
                lastSuccessExecutionAt = feature.lastSuccessExecutionAt,
                lastFailedExecutionAt = feature.lastFailedExecutionAt,
                successExecutionsAmount = feature.successExecutionsAmount,
                failedExecutionsAmount = feature.failedExecutionsAmount
            )
        }

        return meta
    }

    @Transactional
    suspend fun getById(id: UUID): Feature {
        val featureModel = featureRepository.findById(id) ?: throw RuntimeException("Feature not found with id: $id")
        val compilationResult = compilationService.compile(id, featureModel.code)
        return if (compilationResult.success)
            featureModel.toFeature(compilationResult.featureBlock!!)
        else
            throw RuntimeException("Failed to compile feature with id = ${featureModel.id}")
    }

    suspend fun existsById(id: UUID): Boolean {
        return featureRepository.existsById(id)
    }

    fun getAll(): Flow<FeatureModel> {
        return featureRepository.findAll()
            .map { feature ->
                feature.status = if (feature.isBroken) FeatureStatus.BROKEN
                else if (feature.failedExecutionsAmount == 0L) FeatureStatus.LOADING
                else FeatureStatus.LOADING_UNSTABLE
                feature
            }
    }

    fun getAllCompiled(): Flow<Feature> {
        return featureRepository.findAll()
            .filter { featureModel -> !featureModel.isBroken }
            .map { featureModel ->
                val featureCompileJob = compilationService.compileAsync(featureModel.id, featureModel.code)
                featureModel to featureCompileJob
            }
            .buffer()
            .mapNotNull { (featureModel, featureCompileJob) ->
                val compilationResult = featureCompileJob.await()
                if (compilationResult.success)
                    featureModel.toFeature(compilationResult.featureBlock!!)
                else
                    null
            }
    }
}