package ru.ilyasok.StickKs.dsl.trigger

import ru.ilyasok.StickKs.core.context.ExecutionContext
import ru.ilyasok.StickKs.dsl.FeatureBlockBuilder
import ru.ilyasok.StickKs.dsl.FeatureDSL


class TriggerBlock<in E: ExecutionContext>(
    val condition: suspend E.() -> Boolean,
    val execute: suspend E.() -> Unit,
    private val executionContextProvider: () -> E
) {
    private val executionContext by lazy { executionContextProvider() }

    suspend fun checkCondition(): Boolean = condition.invoke(executionContext)

    suspend fun execute() = execute.invoke(executionContext)
}

class TriggerBlockBuilder<E : ExecutionContext> {
    var execute: suspend E.() -> Unit = {}
    var condition: suspend E.() -> Boolean = { true }

    fun build(executionContextProvider: () -> E): TriggerBlock<E> {
        return TriggerBlock(
            execute = execute,
            condition = condition,
            executionContextProvider = executionContextProvider
        )
    }
}

@FeatureDSL
fun<E: ExecutionContext> FeatureBlockBuilder<E>.trigger(block: TriggerBlockBuilder<E>.() -> Unit): TriggerBlock<E> {
    val tb = TriggerBlockBuilder<E>().apply(block).build(this.executionContextProvider)
    this.triggerBlock = tb
    return tb
}

@FeatureDSL
fun<E: ExecutionContext> TriggerBlockBuilder<E>.execute(execute: suspend E.() -> Unit) {
    this.execute = execute
}

@FeatureDSL
fun<E: ExecutionContext> TriggerBlockBuilder<E>.withCondition(condition: suspend E.() -> Boolean) {
    this.condition = condition
}
