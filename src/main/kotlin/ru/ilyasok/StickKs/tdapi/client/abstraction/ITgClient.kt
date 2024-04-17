package ru.ilyasok.StickKs.tdapi.client.abstraction

import ru.ilyasok.StickKs.tdapi.TdApi
import ru.ilyasok.StickKs.tdapi.client.TgClientAuthorizationState
import ru.ilyasok.StickKs.tdapi.client.TgClientParams
import ru.ilyasok.StickKs.tdapi.handler.abstraction.ITdMainClientHandler

interface ITgClient {
    val authorizationState: TgClientAuthorizationState
    val mainHandler: ITdMainClientHandler
    val tgClientParams: TgClientParams
    fun send(query: TdApi.Function<*>)
}