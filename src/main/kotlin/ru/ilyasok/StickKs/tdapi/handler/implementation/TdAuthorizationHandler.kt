package ru.ilyasok.StickKs.tdapi.handler.implementation

import org.springframework.stereotype.Component
import ru.ilyasok.StickKs.tdapi.client.TgClientAuthorizationStateEnum
import ru.ilyasok.StickKs.tdapi.client.abstraction.ITgClient
import ru.ilyasok.StickKs.tdapi.handler.abstraction.ITdAuthorizationHandler

@Component
class TdAuthorizationHandler: ITdAuthorizationHandler {

    override fun onAuthorizationStateChanged(changedState: TgClientAuthorizationStateEnum, client: ITgClient) {
        client.authorizationState.state.set(changedState)
    }
}