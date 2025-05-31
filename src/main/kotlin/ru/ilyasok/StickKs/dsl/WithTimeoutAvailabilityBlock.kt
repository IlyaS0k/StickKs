package ru.ilyasok.StickKs.dsl

import java.time.Instant
import kotlin.time.Duration
import kotlin.time.toJavaDuration

class WithTimePeriodAvailabilityBlock(
    val afterStart: Duration,
    val period: Duration,
    val limit: Long
) : ExecutionControlBlock() {

    override fun control(meta: FeatureMeta): Boolean {
        val now = Instant.now()
        if (limit <= meta.successExecutionsAmount) return false
        if (
            meta.lastSuccessExecutionAt != null &&
            meta.lastSuccessExecutionAt.plus(period.toJavaDuration()).isAfter(now)
        ) return false
        if (meta.lastSuccessExecutionAt == null &&
            meta.createdAt.plus(afterStart.toJavaDuration()).isAfter(now)
        ) return false
        return true
    }
}

@FeatureDslMarker
class WithTimePeriodAvailabilityBlockBuilder {
    var afterStart: Duration = Duration.ZERO
    var period: Duration = Duration.ZERO
    var limit: Long = Long.MAX_VALUE

    fun build(): WithTimePeriodAvailabilityBlock {
        require(limit > 0) { "limit is not greater than 0" }
        return WithTimePeriodAvailabilityBlock(afterStart = afterStart, period = period, limit = limit)
    }
}

@FeatureDslComponent
fun FeatureBlockBuilder<*>.periodically(block: WithTimePeriodAvailabilityBlockBuilder.() -> Unit): WithTimePeriodAvailabilityBlock {
    val a = WithTimePeriodAvailabilityBlockBuilder().apply(block).build()
    this.executionControl = a
    return a
}