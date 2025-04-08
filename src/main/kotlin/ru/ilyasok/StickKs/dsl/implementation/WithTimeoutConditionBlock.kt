package ru.ilyasok.StickKs.dsl.implementation

import kotlin.time.Duration

class WithTimeoutConditionBlock(
    afterStart: Duration,
    timeout: Duration?,
    limit: Int
)

class WithTimeoutConditionBlockBuilder {
    var afterStart: Duration? = null
    var timeout: Duration? = null
    var limit: Int = Int.MAX_VALUE

    fun build(): WithTimeoutConditionBlock {
        require(limit > 0) { "limit is not greater than 0" }
        require(afterStart != null) { "after start block is not initialized" }
        require((timeout == null && limit == ONE) || timeout != null) { "action block is not initialized correctly" }
        return WithTimeoutConditionBlock(afterStart = afterStart!!, timeout = timeout, limit = limit)
    }

    companion object {
        const val ONE = 1
        const val TWO = 2
        const val THREE = 3
        const val FOUR = 4
        const val FIVE = 5
        const val SIX = 6
        const val SEVEN = 7
        const val EIGHT = 8
        const val NINE = 9
        const val TEN = 10
        const val UNLIMITED = Int.MAX_VALUE
    }
}

fun withTimeout(block: WithTimeoutConditionBlockBuilder.() -> Unit): WithTimeoutConditionBlock {
    return WithTimeoutConditionBlockBuilder().apply(block).build()
}