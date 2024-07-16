package ru.ilyasok.StickKs.tdapi.handler.abstraction

import ru.ilyasok.StickKs.tdapi.TdApi.UpdateMessageContent

interface ITdUpdateMessageContentHandler : ITdHandler<UpdateMessageContent> {

    companion object {
        const val DEFAULT_MESSAGE_WAITING_TIMEOUT_MILLIS = 7_000L
    }

    suspend fun getUpdateMessageContentByIdWithTimeout(
        msgId: Long,
        timeoutMillis: Long = DEFAULT_MESSAGE_WAITING_TIMEOUT_MILLIS
    ): UpdateMessageContent?
}