package ru.ilyasok.StickKs.dsl

class OnEventBlock(val events: List<EventBlock<*>>)

class OnEventBlockBuilder() {
    var events: MutableList<EventBlock<*>> = mutableListOf()

    fun build() : OnEventBlock {
        return OnEventBlock(events)
    }
}

@FeatureDSL
fun FeatureBlockBuilder.onEvent(block: OnEventBlockBuilder.() -> Unit): OnEventBlock {
    val onEvent = OnEventBlockBuilder().apply(block).build()
    this.onEvent = onEvent
    return onEvent
}
