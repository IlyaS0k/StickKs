package ru.ilyasok.StickKs.dsl

import java.time.Instant
import kotlin.time.Duration
import kotlin.time.toJavaDuration

class WithTimeoutAvailabilityBlock(
    val afterStart: Duration,
    val timeout: Duration?,
    val limit: Int
) : ExecutionControlBlock() {

    override fun control(meta: FeatureMeta): Boolean {
        val now = Instant.now()
        if (limit <= meta.successExecutionsAmount) return false
        if (timeout != null &&
            meta.lastSuccessExecutionAt != null &&
            meta.lastSuccessExecutionAt.plus(timeout.toJavaDuration()).isAfter(now)
        ) return false
        if (meta.lastSuccessExecutionAt == null &&
            meta.createdAt.plus(afterStart.toJavaDuration()).isAfter(now)
        ) return false
        return true
    }
}

class WithTimeoutAvailabilityBlockBuilder {
    var afterStart: Duration = Duration.ZERO
    var timeout: Duration = Duration.ZERO
    var limit: Int = Int.MAX_VALUE

    fun build(): WithTimeoutAvailabilityBlock {
        require(limit > 0) { "limit is not greater than 0" }
        return WithTimeoutAvailabilityBlock(afterStart = afterStart, timeout = timeout, limit = limit)
    }
}

@FeatureDSL
fun FeatureBlockBuilder.withTimeout(block: WithTimeoutAvailabilityBlockBuilder.() -> Unit): WithTimeoutAvailabilityBlock {
    val a = WithTimeoutAvailabilityBlockBuilder().apply(block).build()
    this.executionControl = a
    return a
}