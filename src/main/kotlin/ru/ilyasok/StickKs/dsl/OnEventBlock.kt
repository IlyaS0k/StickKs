package ru.ilyasok.StickKs.dsl

class OnEventBlock(val event: EventBlock<*>)

class OnEventBlockBuilder() {
    lateinit var event: EventBlock<*>

    fun build() : OnEventBlock {
        assert(::event.isInitialized) {"empty onEvent block"}
        return OnEventBlock(event)
    }
}

@FeatureDSL
fun FeatureBlockBuilder.onEvent(block: OnEventBlockBuilder.() -> Unit): OnEventBlock {
    val onEvent = OnEventBlockBuilder().apply(block).build()
    this.onEvent = onEvent
    return onEvent
}
