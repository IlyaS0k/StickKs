package ru.ilyasok.StickKs.tdapi.client.implementation

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import ru.ilyasok.StickKs.tdapi.Client
import ru.ilyasok.StickKs.tdapi.client.TgClientAuthorizationState
import ru.ilyasok.StickKs.tdapi.client.TgClientAuthorizationStateEnum
import ru.ilyasok.StickKs.tdapi.client.abstraction.ITgClient
import ru.ilyasok.StickKs.tdapi.handler.abstraction.ITdAuthorizationHandler
import ru.ilyasok.StickKs.tdapi.handler.abstraction.ITdMainClientHandler


/**
 * Kotlin adapter of native [Client]
 */

@Component
class TgClient @Autowired constructor(
    override val authorizationState: TgClientAuthorizationState,
    override val mainHandler: ITdMainClientHandler,
    override val authorizationHandler: ITdAuthorizationHandler
) : ITgClient {

    private val adapteeClient: Client

    init {
        mainHandler.forClient(this)
        adapteeClient = createNativeClient(mainHandler)
    }



    private fun createNativeClient(resultHandler: ITdMainClientHandler): Client {
        val client: Client = Client.create(resultHandler, null, null)
        return client
    }

    override fun onAuthorizationStateChanged(authorizationState: TgClientAuthorizationStateEnum) {
        authorizationHandler.onAuthorizationStateChanged(authorizationState, this)
    }

}