package ru.ilyasok.StickKs.tdapi.client.abstraction

import ru.ilyasok.StickKs.tdapi.Client
import ru.ilyasok.StickKs.tdapi.TdApi
import ru.ilyasok.StickKs.tdapi.client.TgClientAuthorizationState
import ru.ilyasok.StickKs.tdapi.client.TgClientParams
import ru.ilyasok.StickKs.tdapi.handler.abstraction.ITdAuthorizationHandler
import ru.ilyasok.StickKs.tdapi.handler.abstraction.ITdMainHandler
import ru.ilyasok.StickKs.tdapi.handler.abstraction.ITdQueryHandler
import ru.ilyasok.StickKs.tdapi.handler.abstraction.ITdUpdateMessageContentHandler
import ru.ilyasok.StickKs.tdapi.model.response.TdQueryHandlerResult

interface ITgClient {
    val adapteeClient: Client
    val authorizationHandler: ITdAuthorizationHandler
    val updateMessageContentHandler: ITdUpdateMessageContentHandler
    val mainHandler: ITdMainHandler
    val tgClientParams: TgClientParams

    fun send(query: TdApi.Function<*>)

    suspend fun getAuthorizationState(): TdQueryHandlerResult<TgClientAuthorizationState, TdApi.Error>

    suspend fun getUser(userId: Long): TdQueryHandlerResult<TdApi.User?, TdApi.Error>

    suspend fun getContacts(): TdQueryHandlerResult<TdApi.Users?, TdApi.Error>

    suspend fun setPhoneNumber(phoneNumber: String): TdQueryHandlerResult<TdApi.Ok?, TdApi.Error>

    suspend fun checkAuthenticationCode(code: String): TdQueryHandlerResult<TdApi.Ok?, TdApi.Error>

    suspend fun <R, E> sendWithCallback(
        query: TdApi.Function<*>, queryHandler: ITdQueryHandler<R, E>
    ): TdQueryHandlerResult<R, E>

    suspend fun <R : TdApi.Object> sendWithCallback(
        query: TdApi.Function<*>
    ): TdQueryHandlerResult<R?, TdApi.Error>

    suspend fun getUpdateMessageContentEventAsync(messageId: Long): TdApi.UpdateMessageContent?
}