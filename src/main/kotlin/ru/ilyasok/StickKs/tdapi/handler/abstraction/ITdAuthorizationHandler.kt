package ru.ilyasok.StickKs.tdapi.handler.abstraction

import ru.ilyasok.StickKs.tdapi.TdApi
import ru.ilyasok.StickKs.tdapi.client.TgClientAuthorizationStateEnum
import ru.ilyasok.StickKs.tdapi.client.abstraction.ITgClient

interface ITdAuthorizationHandler {

    fun onAuthorizationStateChanged(changedState: TgClientAuthorizationStateEnum, client: ITgClient)
}