package ru.ilyasok.StickKs.dsl.implementation.context

import ru.ilyasok.StickKs.dsl.abstraction.IContextInitStrategy
import ru.ilyasok.StickKs.dsl.implementation.event.TgEvent
import ru.ilyasok.StickKs.tdapi.TdApi

class TgEventInitStrategy() : IContextInitStrategy<TgEvent> {

    override fun execute(obj: TgEvent): Context {
        val tgEvent: TgEvent = obj
        val context = Context()
        if (tgEvent.type is TdApi.UpdateNewMessage &&
            tgEvent.type.message.content is TdApi.MessageText
        ) {
            val content = tgEvent.type.message.content as TdApi.MessageText
            context.message = { content.text.text }
        }
        return context
    }
}