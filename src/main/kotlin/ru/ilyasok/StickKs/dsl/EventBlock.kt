package ru.ilyasok.StickKs.dsl

import ru.ilyasok.StickKs.core.context.EventContext
import kotlin.reflect.KClass

open class EventBlock<T: EventContext>(
    val condition: suspend (T) -> Boolean,
    val execute: suspend (T) -> Unit,
    val contextType: KClass<T>
) {
    @Suppress("UNCHECKED_CAST")
    suspend fun execute(eventContext: Any?) {
        if (eventContext != null && eventContext::class == contextType) {
            execute.invoke(eventContext as T)
        }
    }
}

class EventBlockBuilder<T: EventContext>() {
    var execute: suspend (T) -> Unit = {}
    var condition: suspend (T) -> Boolean = { true }

    fun build(contextType: KClass<T>): EventBlock<T> {
        return EventBlock(execute = execute, condition = condition, contextType = contextType)
    }
}

@FeatureDSL
fun<T: EventContext> EventBlockBuilder<T>.execute(execute: suspend (T) -> Unit) {
    this.execute = execute
}

@FeatureDSL
fun<T: EventContext> EventBlockBuilder<T>.withCondition(condition: suspend (T) -> Boolean) {
    this.condition = condition
}
