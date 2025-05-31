package ru.ilyasok.StickKs.dsl.trigger

import ru.ilyasok.StickKs.core.context.ExecutionContext
import ru.ilyasok.StickKs.dsl.FeatureBlockBuilder
import ru.ilyasok.StickKs.dsl.FeatureDslComponent
import ru.ilyasok.StickKs.dsl.FeatureDslMarker


class TriggerBlock<in E: ExecutionContext>(
    val condition: suspend E.() -> Boolean,
    val execute: suspend E.() -> Unit,
    private val executionContextProvider: () -> E
) {
    private val executionContext by lazy { executionContextProvider() }

    suspend fun checkCondition(): Boolean = condition.invoke(executionContext)

    suspend fun execute() = execute.invoke(executionContext)
}

@FeatureDslMarker
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

@FeatureDslComponent
fun<E: ExecutionContext> FeatureBlockBuilder<E>.trigger(block: TriggerBlockBuilder<E>.() -> Unit): TriggerBlock<E> {
    val tb = TriggerBlockBuilder<E>().apply(block).build(this.executionContextProvider)
    this.triggerBlock = tb
    return tb
}

@FeatureDslComponent
fun<E: ExecutionContext> TriggerBlockBuilder<E>.execute(execute: @FeatureDslMarker suspend E.() -> Unit) {
    this.execute = execute
}

@FeatureDslComponent
fun<E: ExecutionContext> TriggerBlockBuilder<E>.withCondition(condition: @FeatureDslMarker suspend E.() -> Boolean) {
    this.condition = condition
}
