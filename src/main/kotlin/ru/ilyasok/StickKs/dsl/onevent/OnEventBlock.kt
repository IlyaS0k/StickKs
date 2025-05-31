package ru.ilyasok.StickKs.dsl.onevent

import ru.ilyasok.StickKs.core.context.ExecutionContext
import ru.ilyasok.StickKs.dsl.FeatureBlockBuilder
import ru.ilyasok.StickKs.dsl.FeatureDslComponent

class OnEventBlock<E: ExecutionContext>(val event: EventBlock<*, E>)

class OnEventBlockBuilder<E: ExecutionContext>() {
    var event: EventBlock<*, E>? = null
    lateinit var executionContextProvider: () -> E

    fun build(executionContextProvider: () -> E): OnEventBlock<E> {
        assert(event != null) { "empty onEvent block" }
        this.executionContextProvider = executionContextProvider
        return OnEventBlock(event!!)
    }
}

@FeatureDslComponent
fun <E: ExecutionContext> FeatureBlockBuilder<E>.onEvent(block: OnEventBlockBuilder<E>.() -> Unit): OnEventBlock<E> {
    val ecp = this.executionContextProvider
    val blockWithSetContext: OnEventBlockBuilder<E>.() -> Unit = {
        this.executionContextProvider = ecp
        block()
    }
    val onEvent = OnEventBlockBuilder<E>().apply(blockWithSetContext).build(ecp)
    this.onEvent = onEvent
    return onEvent
}
