package ru.ilyasok.StickKs.tdapi.feature

import ru.ilyasok.StickKs.dsl.EventBlock
import ru.ilyasok.StickKs.dsl.EventBlockBuilder
import ru.ilyasok.StickKs.dsl.FeatureDSL
import ru.ilyasok.StickKs.dsl.OnEventBlockBuilder

@FeatureDSL
fun OnEventBlockBuilder.newTelegramMessage(block: EventBlockBuilder<TgNewTextMessageContext>.() -> Unit): EventBlock<TgNewTextMessageContext> {
    val e = EventBlockBuilder<TgNewTextMessageContext>().apply(block).build(TgNewTextMessageContext::class)
    this.event = e
    return e
}