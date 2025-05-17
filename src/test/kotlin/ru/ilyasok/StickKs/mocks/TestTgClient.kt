package ru.ilyasok.StickKs.mocks

import org.springframework.stereotype.Service
import ru.ilyasok.StickKs.tdapi.client.TgClientAuthorizationState
import ru.ilyasok.StickKs.tdapi.client.TgClientParams
import ru.ilyasok.StickKs.tdapi.client.abstraction.ITgClient
import ru.ilyasok.StickKs.tdapi.handler.abstraction.ITdMainHandler
import ru.ilyasok.StickKs.tdapi.handler.abstraction.ITdQuery
import ru.ilyasok.StickKs.tdapi.model.response.TdQueryResult
import ru.ilyasok.StickKs.tdapi.TdApi

class TestTgClient(override val mainHandler: ITdMainHandler, override val tgClientParams: TgClientParams) : ITgClient {
    override fun send(query: TdApi.Function<*>) {
        TODO("Not yet implemented")
    }

    override suspend fun getMe(): TdQueryResult<TdApi.User?, TdApi.Error> {
        TODO("Not yet implemented")
    }

    override suspend fun logout(): TdQueryResult<TdApi.Ok?, TdApi.Error> {
        TODO("Not yet implemented")
    }

    override suspend fun getAuthorizationState(): TdQueryResult<TgClientAuthorizationState, TdApi.Error> {
        TODO("Not yet implemented")
    }

    override suspend fun getUser(userId: Long): TdQueryResult<TdApi.User?, TdApi.Error> {
        TODO("Not yet implemented")
    }

    override suspend fun getContacts(): TdQueryResult<TdApi.Users?, TdApi.Error> {
        TODO("Not yet implemented")
    }

    override suspend fun setPhoneNumber(phoneNumber: String): TdQueryResult<TdApi.Ok?, TdApi.Error> {
        TODO("Not yet implemented")
    }

    override suspend fun checkAuthenticationCode(code: String): TdQueryResult<TdApi.Ok?, TdApi.Error> {
        TODO("Not yet implemented")
    }

    override suspend fun sendMessage(chatId: Long, text: String): TdQueryResult<TdApi.Message?, TdApi.Error> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteMessage(
        chatId: Long,
        messageIds: LongArray,
        revoke: Boolean
    ): TdQueryResult<TdApi.Ok?, TdApi.Error> {
        TODO("Not yet implemented")
    }

    override suspend fun <R, E> sendWithCallback(
        query: TdApi.Function<*>,
        queryHandler: ITdQuery<R, E>
    ): TdQueryResult<R, E> {
        TODO("Not yet implemented")
    }

    override suspend fun <R : TdApi.Object> sendWithCallback(query: TdApi.Function<*>): TdQueryResult<R?, TdApi.Error> {
        TODO("Not yet implemented")
    }

    override fun initializeClient() {
        TODO("Not yet implemented")
    }
}