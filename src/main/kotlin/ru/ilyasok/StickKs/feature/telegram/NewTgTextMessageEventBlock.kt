package ru.ilyasok.StickKs.feature.telegram

import ru.ilyasok.StickKs.core.context.ExecutionContext
import ru.ilyasok.StickKs.dsl.onevent.EventBlock
import ru.ilyasok.StickKs.dsl.onevent.EventBlockBuilder
import ru.ilyasok.StickKs.dsl.FeatureDslComponent
import ru.ilyasok.StickKs.dsl.onevent.OnEventBlockBuilder

@FeatureDslComponent
fun<E: ExecutionContext> OnEventBlockBuilder<E>.newTelegramMessage(
    block: EventBlockBuilder<TgNewTextMessageContext, E>.() -> Unit
): EventBlock<TgNewTextMessageContext, E> {
    val e = EventBlockBuilder<TgNewTextMessageContext, E>()
        .apply(block)
        .build(TgNewTextMessageContext::class, this.executionContextProvider)
    this.event = e
    return e
}