package ru.ilyasok.StickKs.repository

import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Repository
import ru.ilyasok.StickKs.model.FeatureErrorsModel
import java.time.Instant
import java.util.UUID

@Repository
class FeatureErrorsRepository(private val databaseClient: DatabaseClient, ) {

    suspend fun insertFeatureError(id: UUID, featureId: UUID, trace: String, timestamp: Instant) {
        databaseClient.sql(
            "INSERT INTO feature_errors (id, feature_id, trace, timestamp) VALUES (:id, :featureId, :trace, :timestamp)"
        )
            .bind("id", id)
            .bind("featureId", featureId)
            .bind("trace", trace)
            .bind("timestamp", timestamp)
            .fetch()
            .rowsUpdated()
            .awaitSingle()
    }

    suspend fun findAllByFeatureId(featureId: UUID, limit: Int): List<FeatureErrorsModel> {
        return databaseClient.sql(
            "SELECT id, feature_id, trace, timestamp FROM feature_errors WHERE :featureId = feature_id ORDER BY timestamp DESC LIMIT :limit"
        )
            .bind("featureId", featureId)
            .bind("limit", limit)
            .map { row, _ ->
                FeatureErrorsModel(
                    id = row.get("id", UUID::class.java)!!,
                    featureId = row.get("feature_id", UUID::class.java)!!,
                    trace = row.get("trace", String::class.java)!!,
                    timestamp = row.get("timestamp", Instant::class.java)!!,
                )
            }
            .all()
            .collectList()
            .awaitSingle()
    }
}
