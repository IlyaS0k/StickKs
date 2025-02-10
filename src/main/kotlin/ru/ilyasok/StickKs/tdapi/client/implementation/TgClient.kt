package ru.ilyasok.StickKs.tdapi.client.implementation

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import ru.ilyasok.StickKs.tdapi.Client
import ru.ilyasok.StickKs.tdapi.TdApi
import ru.ilyasok.StickKs.tdapi.client.TgClientAuthorizationState
import ru.ilyasok.StickKs.tdapi.client.TgClientParams
import ru.ilyasok.StickKs.tdapi.client.abstraction.ITgClient
import ru.ilyasok.StickKs.tdapi.handler.abstraction.ITdAuthorizationHandler
import ru.ilyasok.StickKs.tdapi.handler.abstraction.ITdMainHandler
import ru.ilyasok.StickKs.tdapi.handler.abstraction.ITdQueryHandler
import ru.ilyasok.StickKs.tdapi.handler.abstraction.ITdUpdateMessageContentHandler
import ru.ilyasok.StickKs.tdapi.model.response.TdQueryHandlerResponse

/**
 * Kotlin adapter of native [Client]
 */

@Component
class TgClient @Autowired constructor(
    override val authorizationHandler: ITdAuthorizationHandler,
    override val updateMessageContentHandler: ITdUpdateMessageContentHandler,
    override val mainHandler: ITdMainHandler,
    override val tgClientParams: TgClientParams
) : ITgClient {

    override val adapteeClient = createNativeClient(mainHandler)

    override suspend fun getAuthorizationState() = sendWithCallback(
        TdApi.GetAuthorizationState(),
        object : ITdQueryHandler<TgClientAuthorizationState, TdApi.Error> {
            override fun onResult(obj: TdApi.Object): TgClientAuthorizationState {
                return if (obj is TdApi.AuthorizationState) {
                    TgClientAuthorizationState.convertAuthorizationState(obj)
                } else TgClientAuthorizationState.UNDEFINED
            }

            override fun onError(error: TdApi.Error): TdApi.Error {
                return error
            }
        }
    )

    override fun send(query: TdApi.Function<*>) {
        adapteeClient.send(query, mainHandler)
    }

    override suspend fun <R, E> sendWithCallback(
        query: TdApi.Function<*>,
        queryHandler: ITdQueryHandler<R, E>
    ): TdQueryHandlerResponse<R, E> = coroutineScope {
        val channel = Channel<TdQueryHandlerResponse<R, E>>()
        adapteeClient.send(query) { tdobj ->
            launch {
                if (tdobj is TdApi.Error) {
                    channel.send(TdQueryHandlerResponse.error(queryHandler.onError(tdobj)))
                } else {
                    channel.send(TdQueryHandlerResponse.success(queryHandler.onResult(tdobj)))
                }
            }
        }
        return@coroutineScope channel.receive()
    }

    override suspend fun sendWithCallback(
        query: TdApi.Function<*>,
    ): TdQueryHandlerResponse<TdApi.Object, TdApi.Error> {
        val defaultQueryHandler = object : ITdQueryHandler<TdApi.Object, TdApi.Error> {
            override fun onResult(obj: TdApi.Object): TdApi.Object {
                return obj
            }

            override fun onError(error: TdApi.Error): TdApi.Error {
                return error
            }
        }
        return sendWithCallback(query, defaultQueryHandler)
    }

    override suspend fun getUpdateMessageContentEventAsync(messageId: Long): TdApi.UpdateMessageContent? {
        return updateMessageContentHandler.getUpdateMessageContentByIdWithTimeout(messageId)
    }

    private fun createNativeClient(resultHandler: ITdMainHandler): Client {
        val logVerbosityLevel = TdApi.SetLogVerbosityLevel(0)
        Client.execute(logVerbosityLevel)
        val client = Client.create(resultHandler, null, null)
        return client
    }

}


