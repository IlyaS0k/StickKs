package ru.ilyasok.StickKs.dsl.implementation

class FeatureBlock(
    val action: ActionBlock,
    val condition: ConditionBlock,
)

class FeatureBlockBuilder {
    lateinit var action: ActionBlock
    lateinit var condition: ConditionBlock

    fun build(): FeatureBlock {
        require(::action.isInitialized) { "action block is not initialized" }
        require(::condition.isInitialized) { "condition block is not initialized" }
        return FeatureBlock(action = action, condition = condition)
    }
}

fun feature(block: FeatureBlockBuilder.() -> Unit): FeatureBlock {
    return FeatureBlockBuilder().apply(block).build()
}

