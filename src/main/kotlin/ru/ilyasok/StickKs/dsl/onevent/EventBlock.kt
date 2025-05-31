package ru.ilyasok.StickKs.dsl.onevent

import org.slf4j.LoggerFactory
import ru.ilyasok.StickKs.core.context.EventContext
import ru.ilyasok.StickKs.core.context.ExecutionContext
import ru.ilyasok.StickKs.core.feature.ActivateFeatureEvent
import ru.ilyasok.StickKs.dsl.FeatureDSL
import kotlin.reflect.KClass

open class EventBlock<in T: EventContext, in E: ExecutionContext>(
    val condition: suspend E.(T) -> Boolean,
    val execute: suspend E.(T) -> Unit,
    private val eventContextType: KClass<in T>,
    private val executionContextProvider: () -> E
) {
    companion object {
        private val logger = LoggerFactory.getLogger(EventBlock::class.java)
    }

    private val executionContext by lazy { executionContextProvider() }

    fun isActivated() = eventContextType == ActivateFeatureEvent::class

    fun eventContext() = eventContextType

    @Suppress("UNCHECKED_CAST")
    suspend fun checkCondition(eventContext: Any?): Boolean {
        if (eventContext != null && eventContext::class == eventContextType) {
            return condition.invoke(executionContext, eventContext as T).also { res ->
                logger.debug("Checking condition for event ${System.identityHashCode(eventContext)}; result is $res")
            }
        }
        return false
    }

    @Suppress("UNCHECKED_CAST")
    suspend fun execute(eventContext: Any?) {
        if (eventContext != null && eventContext::class == eventContextType) {
            logger.debug("Executing event ${System.identityHashCode(eventContext)}")
            execute.invoke(executionContext, eventContext as T)
            logger.debug("After executing event ${System.identityHashCode(eventContext)}")
        }
    }
}

class EventBlockBuilder<T: EventContext, E: ExecutionContext>() {
    var execute: suspend E.(T) -> Unit = {}

    var condition: suspend E.(T) -> Boolean = { true }

    fun build(eventContextType: KClass<T>, executionContextProvider: () -> E): EventBlock<T, E> {
        return EventBlock(
            execute = execute,
            condition = condition,
            eventContextType = eventContextType,
            executionContextProvider = executionContextProvider
        )
    }
}


@FeatureDSL
fun<T: EventContext, E: ExecutionContext> EventBlockBuilder<T, E>.execute(execute: suspend E.(T) -> Unit) {
    this.execute = execute
}

@FeatureDSL
fun<T: EventContext, E: ExecutionContext> EventBlockBuilder<T, E>.withCondition(condition: suspend E.(T) -> Boolean) {
    this.condition = condition
}
