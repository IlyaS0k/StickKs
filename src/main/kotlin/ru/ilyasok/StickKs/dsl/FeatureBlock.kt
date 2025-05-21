package ru.ilyasok.StickKs.dsl


class FeatureBlock(
    val name: String,
    val onEvent: OnEventBlock? = null,
    val runBlock: RunBlock? = null,
    val executionControl: ExecutionControlBlock
)

class FeatureBlockBuilder {
    var name: String? = null
    var executionControl: ExecutionControlBlock = AlwaysAvailableBlock(Long.MAX_VALUE)
    var onEvent: OnEventBlock? = null
    var runBlock: RunBlock? = null

    fun build(): FeatureBlock {
        require(onEvent != null || runBlock != null) { "onEventBlock or runBlock must be initialized" }
        require(onEvent == null || runBlock == null) { "onEventBlock and runBlock should not be initialized at the same time" }
        return FeatureBlock(
            name = name ?: "",
            executionControl = executionControl,
            onEvent = onEvent,
            runBlock = runBlock
        )
    }
}

@FeatureDSL
fun feature(block: FeatureBlockBuilder.() -> Unit): FeatureBlock {
    return FeatureBlockBuilder().apply(block).build()
}
