package ru.ilyasok.StickKs.tdapi.handler.implementation

import org.springframework.stereotype.Component
import ru.ilyasok.StickKs.tdapi.TdApi
import ru.ilyasok.StickKs.tdapi.TdApi.UpdateAuthorizationState
import ru.ilyasok.StickKs.tdapi.client.TgClientAuthorizationState.Companion.convertAuthorizationState
import ru.ilyasok.StickKs.tdapi.client.TgClientAuthorizationStateEnum
import ru.ilyasok.StickKs.tdapi.client.abstraction.ITgClient
import ru.ilyasok.StickKs.tdapi.handler.abstraction.ITdHandler

@Component
class TdAuthorizationHandler: ITdHandler {

    override fun handle(client: ITgClient, obj: TdApi.Object) {
        val updateAuthorizationState = obj as UpdateAuthorizationState
        val newState = convertAuthorizationState(updateAuthorizationState.authorizationState)
        client.authorizationState.state.set(newState)
        when (client.authorizationState.state.get()) {
            TgClientAuthorizationStateEnum.UNDEFINED -> TODO()
            TgClientAuthorizationStateEnum.CLOSED -> TODO()
            TgClientAuthorizationStateEnum.WAIT_PHONE_NUMBER -> {

            }
            TgClientAuthorizationStateEnum.WAIT_TDLIB_PARAMETERS -> {
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
            TgClientAuthorizationStateEnum.WAIT_CODE -> {

            }
            TgClientAuthorizationStateEnum.READY -> {

            }
        }
    }
}