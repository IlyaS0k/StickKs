package ru.ilyasok.StickKs.model

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Version
import org.springframework.data.relational.core.mapping.Table
import ru.ilyasok.StickKs.dsl.Feature
import ru.ilyasok.StickKs.dsl.FeatureBlock
import ru.ilyasok.StickKs.dsl.FeatureMeta
import java.time.Instant
import java.util.UUID

@Table("features")
data class FeatureModel(
    @Id
    val id: UUID,

    val name: String,

    val code: String,

    val createdAt: Instant = Instant.now(),

    val lastModifiedAt: Instant = Instant.now(),

    val lastSuccessExecutionAt: Instant? = null,

    val lastFailedExecutionAt: Instant? = null,

    val successExecutionsAmount: Long = 0L,

    val failedExecutionsAmount: Long = 0L,

    @Version
    val version: Long? = null,

) {
    @org.springframework.data.annotation.Transient
    var status: FeatureStatus? = FeatureStatus.STABLE
}

enum class FeatureStatus {
    STABLE,
    UNSTABLE,
    UPDATING,
    CREATING,
    BROKEN,
    LOADING,
    LOADING_UNSTABLE,
}

fun FeatureModel.toFeature(featureBlock: FeatureBlock) = Feature(
    id = this.id,
    version = this.version!!,
    feature = featureBlock,
    meta = FeatureMeta(
        createdAt = this.createdAt,
        lastModifiedAt = this.lastModifiedAt,
        lastSuccessExecutionAt = this.lastSuccessExecutionAt,
        lastFailedExecutionAt = this.lastFailedExecutionAt,
        successExecutionsAmount = this.successExecutionsAmount,
        failedExecutionsAmount = this.failedExecutionsAmount
    ),
)
