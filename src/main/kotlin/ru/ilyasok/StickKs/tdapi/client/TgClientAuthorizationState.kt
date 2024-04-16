package ru.ilyasok.StickKs.tdapi.client

import org.springframework.stereotype.Component
import ru.ilyasok.StickKs.tdapi.TdApi
import java.util.concurrent.atomic.AtomicReference

enum class TgClientAuthorizationStateEnum {
    UNDEFINED,
    CLOSED,
    WAIT_PHONE_NUMBER,
    WAIT_TDLIB_PARAMETERS,
    WAIT_CODE,
    READY
}

@Component
class TgClientAuthorizationState(state: TgClientAuthorizationStateEnum = TgClientAuthorizationStateEnum.UNDEFINED) {
    val state = AtomicReference(state)

    constructor(state: TdApi.AuthorizationState) : this(convertAuthorizationState(state))

    companion object {
        fun convertAuthorizationState(state: TdApi.AuthorizationState): TgClientAuthorizationStateEnum = when (state) {
            is TdApi.AuthorizationStateReady -> TgClientAuthorizationStateEnum.READY
            is TdApi.AuthorizationStateWaitPhoneNumber -> TgClientAuthorizationStateEnum.WAIT_PHONE_NUMBER
            is TdApi.AuthorizationStateClosed -> TgClientAuthorizationStateEnum.CLOSED
            is TdApi.AuthorizationStateWaitTdlibParameters -> TgClientAuthorizationStateEnum.WAIT_TDLIB_PARAMETERS
            is TdApi.AuthorizationStateWaitCode -> TgClientAuthorizationStateEnum.WAIT_CODE
            else -> TgClientAuthorizationStateEnum.UNDEFINED
        }
    }
}