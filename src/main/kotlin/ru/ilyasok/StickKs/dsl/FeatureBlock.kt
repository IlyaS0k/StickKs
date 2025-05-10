package ru.ilyasok.StickKs.dsl


class FeatureBlock(
    val name: String,
    val onEvent: OnEventBlock,
    val availability: AvailabilityBlock,
)

class FeatureBlockBuilder {
    var name: String? = null
    var availability: AvailabilityBlock = AlwaysAvailableAvailabilityBlock()
    lateinit var onEvent: OnEventBlock

    fun build(): FeatureBlock {
        require(::onEvent.isInitialized) { "onEvent block is not initialized" }
        return FeatureBlock(
            onEvent = onEvent, availability = availability, name = name ?: "",)
    }
}

@FeatureDSL
fun feature(block: FeatureBlockBuilder.() -> Unit): FeatureBlock {
    return FeatureBlockBuilder().apply(block).build()
}

