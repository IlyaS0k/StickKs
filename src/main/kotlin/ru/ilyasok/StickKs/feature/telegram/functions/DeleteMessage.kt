package ru.ilyasok.StickKs.feature.telegram.functions

import ru.ilyasok.StickKs.core.utils.SpringContext
import ru.ilyasok.StickKs.dsl.FeatureDSL
import ru.ilyasok.StickKs.feature.telegram.TgNewTextMessageContext

@FeatureDSL
suspend fun deleteMessage(chatId: Long? = null, messageId: Long? = null, forAllMembers: Boolean = true) {
    assert(chatId != null && messageId != null) { "chatId and messageId must not be null" }
    val tgFunctions = SpringContext.getBean(TgFunctions::class.java)
    tgFunctions.deleteMessage(chatId = chatId!!, messageId = messageId!!, revoke = forAllMembers)
}

@FeatureDSL
suspend fun deleteMessage(message: TgNewTextMessageContext, forAllMembers: Boolean = true) {
    val tgFunctions = SpringContext.getBean(TgFunctions::class.java)
    tgFunctions.deleteMessage(chatId = message.chatId, messageId = message.id, revoke = forAllMembers)
}

suspend fun TgFunctions.deleteMessage(chatId: Long, messageId: Long, revoke: Boolean) {
    client.deleteMessage(chatId, longArrayOf(messageId), revoke).handle {
        onError = {
            throw RuntimeException("Failed to delete message: ${it.message}")
        }
    }
}