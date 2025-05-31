package ru.ilyasok.StickKs.dsl

import ru.ilyasok.StickKs.core.context.ExecutionContext
import ru.ilyasok.StickKs.dsl.onevent.OnEventBlock
import ru.ilyasok.StickKs.dsl.trigger.TriggerBlock


class FeatureBlock(
    val name: String,
    val onEvent: OnEventBlock<*>? = null,
    val triggerBlock: TriggerBlock<*>? = null,
    val executionControl: ExecutionControlBlock,
    val activated: Boolean = false
)

class FeatureBlockBuilder<E: ExecutionContext> {
    var name: String? = null
    var executionControl: ExecutionControlBlock = AlwaysAvailableBlock(Long.MAX_VALUE)
    var onEvent: OnEventBlock<E>? = null
    var triggerBlock: TriggerBlock<E>? = null
    var context: E = ExecutionContext() as E
    var executionContextProvider = { context }

    fun build(): FeatureBlock {
        require(onEvent != null || triggerBlock != null) { "onEventBlock or TriggerBlock must be initialized" }
        require(onEvent == null || triggerBlock == null) { "onEventBlock and TriggerBlock should not be initialized at the same time" }
        var activated = false
        if (triggerBlock != null) activated = true
        if (onEvent?.event?.isActivated() == true) activated = true
        if (activated) {
            require(
                executionControl::class in listOf(
                    WithTimePeriodAvailabilityBlock::class,
                    OnScheduleAvailabilityBlock::class
                )
            ) { "Need to specify another execution control block to activated feature" }
        }

        return FeatureBlock(
            name = name ?: "",
            executionControl = executionControl,
            onEvent = onEvent,
            triggerBlock = triggerBlock,
            activated = activated,
        )
    }
}

@FeatureDSL
fun<E: ExecutionContext> feature(block: FeatureBlockBuilder<E>.() -> Unit): FeatureBlock {
    return FeatureBlockBuilder<E>().apply(block).build()
}
