package ru.ilyasok.StickKs.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.ilyasok.StickKs.model.FeatureErrorsModel
import ru.ilyasok.StickKs.repository.FeatureErrorsRepository
import java.time.Instant
import java.util.UUID

@Service
class FeatureErrorsService(
    private val featureErrorsRepository: FeatureErrorsRepository
) {

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java)
    }

    suspend fun getFeatureErrors(id: UUID, limit: Int): List<FeatureErrorsModel> {
        return featureErrorsRepository.findAllByFeatureId(id, limit)
    }

    suspend fun updateFeatureErrors(featureId: UUID, trace: String): FeatureErrorsModel? {
        try {
            featureErrorsRepository.insertFeatureError(UUID.randomUUID(), featureId, trace, Instant.now())
            logger.info("Successfully updated feature error info, featureID: $featureId")
        } catch (e: Exception) {
            logger.error(e.message, e)
        }
        return null
    }
}