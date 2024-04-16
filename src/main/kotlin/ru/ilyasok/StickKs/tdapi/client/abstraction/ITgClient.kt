package ru.ilyasok.StickKs.tdapi.client.abstraction

import ru.ilyasok.StickKs.tdapi.client.TgClientAuthorizationState
import ru.ilyasok.StickKs.tdapi.client.TgClientAuthorizationStateEnum
import ru.ilyasok.StickKs.tdapi.handler.abstraction.ITdAuthorizationHandler
import ru.ilyasok.StickKs.tdapi.handler.abstraction.ITdMainClientHandler

interface ITgClient {
    val authorizationState: TgClientAuthorizationState
    val mainHandler: ITdMainClientHandler
    val authorizationHandler: ITdAuthorizationHandler

    fun onAuthorizationStateChanged(authorizationState: TgClientAuthorizationStateEnum)
}