package ru.ilyasok.StickKs.tdapi.handler.implementation

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.*
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component
import ru.ilyasok.StickKs.core.event.Event
import ru.ilyasok.StickKs.core.event.source.IEventSource
import ru.ilyasok.StickKs.tdapi.TdApi
import ru.ilyasok.StickKs.tdapi.client.abstraction.ITgClient
import ru.ilyasok.StickKs.tdapi.feature.context.TgNewTextMessageContext
import ru.ilyasok.StickKs.tdapi.feature.event.TgReceiveTextMessageEvent
import ru.ilyasok.StickKs.tdapi.handler.abstraction.ITdHandler

@Component
class TdFeatureHandler(@Lazy private val client: ITgClient) : ITdHandler, IEventSource {

    @Autowired
    @Lazy
    private lateinit var self: TdFeatureHandler

    override suspend fun handle(obj: TdApi.Object) = coroutineScope {
        try {
            async(Dispatchers.IO) {
                self.publishEvent(buildEvent(obj))
            }
            Unit
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private suspend fun buildEvent(args: Any?): Event? = when (args) {
        is TdApi.UpdateNewMessage -> {
            val tgEvent = args
            if (tgEvent.message.content is TdApi.MessageText) {
                val content = tgEvent.message.content as TdApi.MessageText
                TgReceiveTextMessageEvent(
                    context = TgNewTextMessageContext(
                        message = content.text.text,
                        user = client.getUser(tgEvent.message.chatId).handle { onSuccess = { it } }
                    )
                )
            } else null
        }
        else -> null
    }
}