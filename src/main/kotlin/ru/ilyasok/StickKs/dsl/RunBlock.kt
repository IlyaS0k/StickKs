package ru.ilyasok.StickKs.dsl


class TriggerBlock(
    val condition: suspend () -> Boolean,
    val execute: suspend () -> Unit,
) {
    suspend fun checkCondition(): Boolean = condition.invoke()

    suspend fun execute() = execute.invoke()
}

class TriggerBlockBuilder {
    var execute: suspend () -> Unit = {}
    var condition: suspend () -> Boolean = { true }

    fun build(): TriggerBlock {
        return TriggerBlock(
            execute = execute,
            condition = condition
        )
    }
}

@FeatureDSL
fun FeatureBlockBuilder.trigger(block: TriggerBlockBuilder.() -> Unit): TriggerBlock {
    val tb = TriggerBlockBuilder().apply(block).build()
    this.triggerBlock = tb
    return tb
}

@FeatureDSL
fun TriggerBlockBuilder.execute(execute: suspend () -> Unit) {
    this.execute = execute
}

@FeatureDSL
fun TriggerBlockBuilder.withCondition(condition: suspend () -> Boolean) {
    this.condition = condition
}
