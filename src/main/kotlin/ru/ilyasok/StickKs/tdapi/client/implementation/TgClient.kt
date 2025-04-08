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
import ru.ilyasok.StickKs.tdapi.handler.abstraction.ITdMainHandler
import ru.ilyasok.StickKs.tdapi.handler.abstraction.ITdQuery
import ru.ilyasok.StickKs.tdapi.model.response.TdQueryHandlerResult

/**
 * Kotlin adapter of native [Client]
 */

@Component
class TgClient @Autowired constructor(
    override val mainHandler: ITdMainHandler,
    override val tgClientParams: TgClientParams
) : ITgClient {

    override val adapteeClient = createNativeClient(mainHandler)

    override suspend fun getAuthorizationState() = sendWithCallback(
        TdApi.GetAuthorizationState(),
        object : ITdQuery<TgClientAuthorizationState, TdApi.Error> {
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

    override suspend fun getUser(userId: Long): TdQueryHandlerResult<TdApi.User?, TdApi.Error> =
        sendWithCallback(TdApi.GetUser(userId))

    override suspend fun getContacts(): TdQueryHandlerResult<TdApi.Users?, TdApi.Error> =
        sendWithCallback(TdApi.GetContacts())

    override suspend fun setPhoneNumber(phoneNumber: String): TdQueryHandlerResult<TdApi.Ok?, TdApi.Error> =
        sendWithCallback(TdApi.SetAuthenticationPhoneNumber(phoneNumber, null))

    override suspend fun checkAuthenticationCode(code: String): TdQueryHandlerResult<TdApi.Ok?, TdApi.Error> =
        sendWithCallback(TdApi.CheckAuthenticationCode(code))

    override fun send(query: TdApi.Function<*>) {
        adapteeClient.send(query, mainHandler)
    }

    override suspend fun <R, E> sendWithCallback(
        query: TdApi.Function<*>,
        queryHandler: ITdQuery<R, E>
    ): TdQueryHandlerResult<R, E> = coroutineScope {
        val channel = Channel<TdQueryHandlerResult<R, E>>()
        adapteeClient.send(query) { tdobj ->
            launch {
                if (tdobj is TdApi.Error) {
                    channel.send(TdQueryHandlerResult.error(queryHandler.onError(tdobj)))
                } else {
                    channel.send(TdQueryHandlerResult.success(queryHandler.onResult(tdobj)))
                }
            }
        }
        return@coroutineScope channel.receive()
    }

    override suspend fun <R: TdApi.Object> sendWithCallback(
        query: TdApi.Function<*>,
    ): TdQueryHandlerResult<R?, TdApi.Error> {
        val defaultQueryHandler = object : ITdQuery<R?, TdApi.Error> {
            override fun onResult(obj: TdApi.Object): R? {
                return obj as? R
            }

            override fun onError(error: TdApi.Error): TdApi.Error {
                return error
            }
        }
        return sendWithCallback(query, defaultQueryHandler)
    }

    private fun createNativeClient(resultHandler: ITdMainHandler): Client {
        val logVerbosityLevel = TdApi.SetLogVerbosityLevel(0)
        Client.execute(logVerbosityLevel)
        val client = Client.create(resultHandler, null, null)
        return client
    }

}


