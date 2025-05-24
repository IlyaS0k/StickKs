package ru.ilyasok.StickKs.tdapi.handler.implementation

import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component
import ru.ilyasok.StickKs.tdapi.TdApi
import ru.ilyasok.StickKs.tdapi.client.TgClientAuthorizationState
import ru.ilyasok.StickKs.tdapi.client.TgClientAuthorizationState.Companion.convertAuthorizationState
import ru.ilyasok.StickKs.tdapi.client.abstraction.ITgClient
import ru.ilyasok.StickKs.tdapi.handler.abstraction.ITdHandler
import ru.ilyasok.StickKs.tdapi.utils.TdEqRelation
import ru.ilyasok.StickKs.tdapi.utils.TdEquals

@Component
class TdAuthorizationHandler(
    @Lazy
    private val client: ITgClient
): ITdHandler
{

    companion object {
        private val logger = LoggerFactory.getLogger(TdAuthorizationHandler::class.java)
    }

    override suspend fun handle(event: TdApi.Object) {
        if (TdEqRelation.TD_EQUALS == TdEquals.check(event, TdApi.UpdateAuthorizationState::class.java)) {
            val authState = event as TdApi.UpdateAuthorizationState
            val newState = convertAuthorizationState(authState.authorizationState)
            when (newState) {
                TgClientAuthorizationState.WAIT_TDLIB_PARAMETERS -> {
                    val setParamsRequest = TdApi.SetTdlibParameters()
                    setParamsRequest.databaseDirectory = client.tgClientParams.databaseDirectory
                    setParamsRequest.useMessageDatabase = client.tgClientParams.useMessageDatabase!!
                    setParamsRequest.useSecretChats = client.tgClientParams.useSecretChats!!
                    setParamsRequest.apiId = client.tgClientParams.apiId!!
                    setParamsRequest.apiHash = client.tgClientParams.apiHash
                    setParamsRequest.systemLanguageCode = client.tgClientParams.systemLanguageCode
                    setParamsRequest.deviceModel = client.tgClientParams.deviceModel
                    setParamsRequest.applicationVersion = client.tgClientParams.applicationVersion
                    client.send(setParamsRequest)
                }
                TgClientAuthorizationState.CLOSED -> {
                    client.initializeClient()
                }
                else -> return
            }
            logger.info("Updated auth state: ${newState.name}")
        }
    }
}