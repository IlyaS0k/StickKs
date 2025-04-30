package ru.ilyasok.StickKs.tdapi.client.abstraction

import ru.ilyasok.StickKs.tdapi.Client
import ru.ilyasok.StickKs.tdapi.TdApi
import ru.ilyasok.StickKs.tdapi.client.TgClientAuthorizationState
import ru.ilyasok.StickKs.tdapi.client.TgClientParams
import ru.ilyasok.StickKs.tdapi.handler.abstraction.ITdMainHandler
import ru.ilyasok.StickKs.tdapi.handler.abstraction.ITdQuery
import ru.ilyasok.StickKs.tdapi.model.response.TdQueryResult

interface ITgClient {
    val adapteeClient: Client
    val mainHandler: ITdMainHandler
    val tgClientParams: TgClientParams

    fun send(query: TdApi.Function<*>)

    suspend fun getMe(): TdQueryResult<TdApi.Users?, TdApi.Error>

    suspend fun getAuthorizationState(): TdQueryResult<TgClientAuthorizationState, TdApi.Error>

    suspend fun getUser(userId: Long): TdQueryResult<TdApi.User?, TdApi.Error>

    suspend fun getContacts(): TdQueryResult<TdApi.Users?, TdApi.Error>

    suspend fun setPhoneNumber(phoneNumber: String): TdQueryResult<TdApi.Ok?, TdApi.Error>

    suspend fun checkAuthenticationCode(code: String): TdQueryResult<TdApi.Ok?, TdApi.Error>

    suspend fun <R, E> sendWithCallback(
        query: TdApi.Function<*>, queryHandler: ITdQuery<R, E>
    ): TdQueryResult<R, E>

    suspend fun <R : TdApi.Object> sendWithCallback(
        query: TdApi.Function<*>
    ): TdQueryResult<R?, TdApi.Error>
}