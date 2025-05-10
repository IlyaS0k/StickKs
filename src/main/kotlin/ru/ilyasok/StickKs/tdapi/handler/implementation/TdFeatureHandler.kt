package ru.ilyasok.StickKs.tdapi.handler.implementation

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component
import ru.ilyasok.StickKs.core.context.EventContext
import ru.ilyasok.StickKs.core.event.source.IEventSource
import ru.ilyasok.StickKs.tdapi.TdApi
import ru.ilyasok.StickKs.tdapi.client.abstraction.ITgClient
import ru.ilyasok.StickKs.tdapi.feature.TgNewTextMessageContext
import ru.ilyasok.StickKs.tdapi.handler.abstraction.ITdHandler

@Component
class TdFeatureHandler(@Lazy private val client: ITgClient) : ITdHandler, IEventSource {

    @Autowired
    @Lazy
    private lateinit var self: TdFeatureHandler

    override suspend fun handle(obj: TdApi.Object) = coroutineScope {
        try {
            async {
                self.publishEvent(buildEvent(obj))
            }
            Unit
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private suspend fun buildEvent(args: Any?): EventContext? = when (args) {
        is TdApi.UpdateNewMessage -> {
            val tgEvent = args
            val msg = tgEvent.message
            if (msg.content is TdApi.MessageText && msg.senderId is TdApi.MessageSenderUser) {
                val sender = msg.senderId as TdApi.MessageSenderUser
                val content = tgEvent.message.content as TdApi.MessageText
                TgNewTextMessageContext(
                    messageText = content.text.text,
                    sender = client.getUser(sender.userId).handle { onSuccess = { it } } ?: TdApi.User()
                )
            } else null
        }
        else -> null
    }
}