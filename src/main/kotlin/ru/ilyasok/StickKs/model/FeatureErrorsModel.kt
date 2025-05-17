package ru.ilyasok.StickKs.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.util.UUID


@Table("feature_errors")
data class FeatureErrorsModel(
    @Id
    val id: UUID,

    val featureId: UUID,

    val timestamp: Instant,

    val trace: String
)