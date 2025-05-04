package ru.ilyasok.StickKs.dsl

import java.util.UUID

class FeatureBlock(
    val name: String,
    val onEvent: OnEventBlock,
    val configuration: ConfigurationBlock,
)

class FeatureBlockBuilder {
    var name: String? = null
    var configuration: ConfigurationBlock = AlwaysAvailableConfigurationBlock()
    lateinit var onEvent: OnEventBlock

    fun build(): FeatureBlock {
        require(::onEvent.isInitialized) { "onEvent block is not initialized" }
        name = if (name.isNullOrBlank()) "Feature#${UUID.randomUUID()}" else name
        return FeatureBlock(onEvent = onEvent, configuration = configuration, name = name!!)
    }
}

@FeatureDSL
fun feature(block: FeatureBlockBuilder.() -> Unit): FeatureBlock {
    return FeatureBlockBuilder().apply(block).build()
}

