package ru.ilyasok.StickKs.tdapi.handler.implementation

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.springframework.stereotype.Component
import ru.ilyasok.StickKs.tdapi.TdApi.UpdateMessageContent
import ru.ilyasok.StickKs.tdapi.client.abstraction.ITgClient
import ru.ilyasok.StickKs.tdapi.handler.abstraction.ITdUpdateMessageContentHandler

@Component
class TdUpdateMessageContentHandler : ITdUpdateMessageContentHandler {

    companion object {
        private const val DEFAULT_MESSAGE_STORAGE_TIMEOUT_MILLIS = 100_000L
    }

    private val msgUpdatesById: MutableMap<Long, UpdateMessageContent> = mutableMapOf()

    private val mutex = Mutex()

    private val notificationChannel = Channel<Long>()

    private suspend fun addUpdateMessageContentWithTimeout(
        msgUpdateContent: UpdateMessageContent,
        timeoutMillis: Long = DEFAULT_MESSAGE_STORAGE_TIMEOUT_MILLIS
    ): Unit = coroutineScope {
        mutex.withLock {
            msgUpdatesById[msgUpdateContent.messageId] = msgUpdateContent
        }
        notificationChannel.send(msgUpdateContent.messageId)
        launch {
            delay(timeoutMillis)
            mutex.withLock {
                msgUpdatesById.remove(msgUpdateContent.messageId)
            }
        }
    }

    override suspend fun getUpdateMessageContentByIdWithTimeout(
        msgId: Long,
        timeoutMillis: Long
    ): UpdateMessageContent? = withTimeoutOrNull(timeoutMillis) {
        mutex.withLock {
            if (msgUpdatesById.containsKey(msgId)) {
                val result = msgUpdatesById[msgId]
                msgUpdatesById.remove(msgId)
                return@withTimeoutOrNull result
            }
        }
        var result: UpdateMessageContent? = null
        while (true) {
            val receivedId = notificationChannel.receive()
            if (receivedId == msgId) {
                mutex.withLock {
                    result = msgUpdatesById[receivedId]
                }
                break
            }
        }
        return@withTimeoutOrNull result
    }

    override fun handle(client: ITgClient, obj: UpdateMessageContent) {
        GlobalScope.launch {
            addUpdateMessageContentWithTimeout(obj)
        }
    }
}