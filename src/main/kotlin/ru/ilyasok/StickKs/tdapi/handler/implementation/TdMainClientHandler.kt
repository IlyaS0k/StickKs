package ru.ilyasok.StickKs.tdapi.handler.implementation;

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component
import ru.ilyasok.StickKs.tdapi.TdApi
import ru.ilyasok.StickKs.tdapi.client.TgClientAuthorizationState
import ru.ilyasok.StickKs.tdapi.client.abstraction.ITgClient
import ru.ilyasok.StickKs.tdapi.handler.abstraction.ITdMainClientHandler
import ru.ilyasok.StickKs.tdapi.utils.TdEqRelation
import ru.ilyasok.StickKs.tdapi.utils.TdEquals

@Component
class TdMainClientHandler @Autowired constructor(
    override val authorizationHandler: TdAuthorizationHandler,
): ITdMainClientHandler {

    override lateinit var client: ITgClient
     @Lazy @Autowired set

    override fun onResult(obj: TdApi.Object) {
         when (TdEqRelation.TD_EQUALS) {
             TdEquals.check(obj, TdApi.UpdateAuthorizationState::class.java) -> {
                 val updateAuthorizationState = obj as TdApi.UpdateAuthorizationState
                 val authorizationStateAsEnum =
                     TgClientAuthorizationState.convertAuthorizationState(updateAuthorizationState.authorizationState)
                 authorizationHandler.onAuthorizationStateChanged(authorizationStateAsEnum, client)
             }
             else -> TODO()
         }
    }
}
