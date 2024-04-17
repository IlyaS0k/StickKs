package ru.ilyasok.StickKs.tdapi.client.implementation

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import ru.ilyasok.StickKs.tdapi.Client
import ru.ilyasok.StickKs.tdapi.TdApi
import ru.ilyasok.StickKs.tdapi.client.TgClientAuthorizationState
import ru.ilyasok.StickKs.tdapi.client.TgClientParams
import ru.ilyasok.StickKs.tdapi.client.abstraction.ITgClient
import ru.ilyasok.StickKs.tdapi.handler.abstraction.ITdMainClientHandler


/**
 * Kotlin adapter of native [Client]
 */

@Component
class TgClient @Autowired constructor(
    override val authorizationState: TgClientAuthorizationState,
    override val mainHandler: ITdMainClientHandler,
    override val tgClientParams: TgClientParams
) : ITgClient {

    private val adapteeClient: Client

    init {
        adapteeClient = createNativeClient(mainHandler)
    }

    override fun send(query: TdApi.Function<*>) {
        adapteeClient.send(query, mainHandler)
    }

    private fun createNativeClient(resultHandler: ITdMainClientHandler): Client {
        val client: Client = Client.create(resultHandler, null, null)
        return client
    }


}