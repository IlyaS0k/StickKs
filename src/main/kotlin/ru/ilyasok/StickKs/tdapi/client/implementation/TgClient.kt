package ru.ilyasok.StickKs.tdapi.client.implementation

import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import ru.ilyasok.StickKs.tdapi.Client
import ru.ilyasok.StickKs.tdapi.TdApi
import ru.ilyasok.StickKs.tdapi.client.TgClientAuthorizationState
import ru.ilyasok.StickKs.tdapi.client.TgClientParams
import ru.ilyasok.StickKs.tdapi.client.abstraction.ITgClient
import ru.ilyasok.StickKs.tdapi.handler.abstraction.ITdMainHandler
import ru.ilyasok.StickKs.tdapi.handler.abstraction.ITdQuery
import ru.ilyasok.StickKs.tdapi.model.response.TdQueryResult
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Kotlin adapter of native [Client]
 */

@Component
@Profile("!test")
class TgClient(
    override val mainHandler: ITdMainHandler,
    @param:Value("\${tdlib.config.log-verbosity-level}")
    private val logVerbosityLevel: Int,
    override val tgClientParams: TgClientParams
) : ITgClient {

    private var nativeClient: Client? = null

    @PostConstruct
    fun postConstruct() {
        initializeClient()
    }

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

    override suspend fun getUser(userId: Long): TdQueryResult<TdApi.User?, TdApi.Error> =
        sendWithCallback(TdApi.GetUser(userId))

    override suspend fun getContacts(): TdQueryResult<TdApi.Users?, TdApi.Error> =
        sendWithCallback(TdApi.GetContacts())

    override suspend fun setPhoneNumber(phoneNumber: String): TdQueryResult<TdApi.Ok?, TdApi.Error> =
        sendWithCallback(TdApi.SetAuthenticationPhoneNumber(phoneNumber, null))

    override suspend fun checkAuthenticationCode(code: String): TdQueryResult<TdApi.Ok?, TdApi.Error> =
        sendWithCallback(TdApi.CheckAuthenticationCode(code))

    override suspend fun sendMessage(chatId: Long, text: String): TdQueryResult<TdApi.Message?, TdApi.Error> =
        sendWithCallback(
            TdApi.SendMessage(
                chatId,
                0L,
                null,
                null,
                null,
                TdApi.InputMessageText(
                    TdApi.FormattedText(text, null),
                    null,
                    true
                )
            )
        )

    override suspend fun deleteMessage(
        chatId: Long,
        messageIds: LongArray,
        revoke: Boolean
    ): TdQueryResult<TdApi.Ok?, TdApi.Error> = sendWithCallback(TdApi.DeleteMessages(chatId, messageIds, revoke))

    override fun send(query: TdApi.Function<*>) {
        nativeClient!!.send(query, mainHandler)
    }

    override suspend fun getMe(): TdQueryResult<TdApi.User?, TdApi.Error> =
        sendWithCallback(TdApi.GetMe())

    override suspend fun logout(): TdQueryResult<TdApi.Ok?, TdApi.Error> =
        sendWithCallback(TdApi.LogOut())

    override suspend fun <R, E> sendWithCallback(
        query: TdApi.Function<*>,
        queryHandler: ITdQuery<R, E>
    ): TdQueryResult<R, E> = suspendCoroutine { cont ->
        nativeClient!!.send(query) { tdobj ->
            if (tdobj is TdApi.Error) {
                cont.resume(TdQueryResult.error(queryHandler.onError(tdobj)))
            } else {
                cont.resume(TdQueryResult.success(queryHandler.onResult(tdobj)))
            }
        }
    }

    override suspend fun <R : TdApi.Object> sendWithCallback(
        query: TdApi.Function<*>,
    ): TdQueryResult<R?, TdApi.Error> {
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

    override fun initializeClient() {
        val logVerbosityLevel = TdApi.SetLogVerbosityLevel(logVerbosityLevel)
        Client.execute(logVerbosityLevel)
        nativeClient = Client.create(mainHandler, null, null)
    }

}


