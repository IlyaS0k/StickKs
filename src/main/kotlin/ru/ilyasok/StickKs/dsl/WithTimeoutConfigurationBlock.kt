package ru.ilyasok.StickKs.dsl

import kotlin.time.Duration

class WithTimeoutConfigurationBlock(
    val afterStart: Duration,
    val timeout: Duration?,
    val limit: Int
) : ConfigurationBlock()

class WithTimeoutConfigurationBlockBuilder {
    var afterStart: Duration? = null
    var timeout: Duration? = null
    var limit: Int = Int.MAX_VALUE

    fun build(): WithTimeoutConfigurationBlock {
        require(limit > 0) { "limit is not greater than 0" }
        require((timeout == null && limit == 1) || timeout != null) { "action block is not initialized correctly" }
        return WithTimeoutConfigurationBlock(afterStart = afterStart ?: Duration.ZERO, timeout = timeout, limit = limit)
    }
}

@FeatureDSL
fun FeatureBlockBuilder.withTimeout(block: WithTimeoutConfigurationBlockBuilder.() -> Unit): WithTimeoutConfigurationBlock {
    val conf = WithTimeoutConfigurationBlockBuilder().apply(block).build()
    this.configuration = conf
    return conf
}