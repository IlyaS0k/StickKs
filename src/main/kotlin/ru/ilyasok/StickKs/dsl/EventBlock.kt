package ru.ilyasok.StickKs.dsl

import org.slf4j.LoggerFactory
import ru.ilyasok.StickKs.core.context.EventContext
import kotlin.reflect.KClass

open class EventBlock<T: EventContext>(
    val condition: suspend (T) -> Boolean,
    val execute: suspend (T) -> Unit,
    val contextType: KClass<T>
) {
    companion object {
        private val logger = LoggerFactory.getLogger(EventBlock::class.java)
    }

    @Suppress("UNCHECKED_CAST")
    suspend fun checkCondition(eventContext: Any?): Boolean {
        if (eventContext != null && eventContext::class == contextType) {
            return condition.invoke(eventContext as T).also { res ->
                logger.debug("Checking condition for event ${System.identityHashCode(eventContext)}; result is $res")
            }
        }
        return false
    }

    @Suppress("UNCHECKED_CAST")
    suspend fun execute(eventContext: Any?) {
        if (eventContext != null && eventContext::class == contextType) {
            logger.debug("Executing event ${System.identityHashCode(eventContext)}")
            execute.invoke(eventContext as T)
            logger.debug("After executing event ${System.identityHashCode(eventContext)}")
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
