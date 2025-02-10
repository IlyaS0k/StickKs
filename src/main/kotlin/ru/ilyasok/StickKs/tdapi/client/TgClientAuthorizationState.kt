package ru.ilyasok.StickKs.tdapi.client

import ru.ilyasok.StickKs.tdapi.TdApi

enum class TgClientAuthorizationState {
    UNDEFINED,
    CLOSED,
    WAIT_PHONE_NUMBER,
    WAIT_TDLIB_PARAMETERS,
    WAIT_CODE,
    READY;

    companion object {
        fun convertAuthorizationState(state: TdApi.AuthorizationState): TgClientAuthorizationState = when (state) {
            is TdApi.AuthorizationStateReady -> READY
            is TdApi.AuthorizationStateWaitPhoneNumber -> WAIT_PHONE_NUMBER
            is TdApi.AuthorizationStateClosed -> CLOSED
            is TdApi.AuthorizationStateWaitTdlibParameters -> WAIT_TDLIB_PARAMETERS
            is TdApi.AuthorizationStateWaitCode -> WAIT_CODE
            else -> UNDEFINED
        }
    }
}