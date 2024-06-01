package ru.ilyasok.StickKs.controller.implementation

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import ru.ilyasok.StickKs.controller.abstraction.ITgClientAuthorizationController
import ru.ilyasok.StickKs.tdapi.TdApi
import ru.ilyasok.StickKs.tdapi.client.TgClientAuthorizationStateEnum
import ru.ilyasok.StickKs.tdapi.client.abstraction.ITgClient
import ru.ilyasok.StickKs.tdapi.handler.abstraction.ITdQueryHandler
import ru.ilyasok.StickKs.tdapi.model.ErrorTdQueryHandlerResponse
import ru.ilyasok.StickKs.tdapi.model.SuccessTdQueryHandlerResponse


@Controller
@RequestMapping("/auth")
class TgClientAuthorizationController @Autowired constructor(val client: ITgClient) : ITgClientAuthorizationController {


    @GetMapping("/by-phone")
    override suspend fun authByPhone(): String {
        return "authorization-by-phone"
    }

    @PostMapping("/submit-phone")
    @ResponseBody
    override suspend fun submitPhone(@RequestBody phoneNumber: String?): ResponseEntity<String> {
        val setPhoneCallback = client.sendWithCallback(TdApi.SetAuthenticationPhoneNumber(phoneNumber, null))
        return when(setPhoneCallback) {
            is SuccessTdQueryHandlerResponse -> ResponseEntity.ok("The phone number has been set")

            is ErrorTdQueryHandlerResponse -> ResponseEntity.status(setPhoneCallback.error.code)
                                                            .body(setPhoneCallback.error.message)
        }
    }

    @PostMapping("/submit-login-code")
    @ResponseBody
    override suspend fun submitLoginCode(@RequestBody loginCode: String?): ResponseEntity<String> {
        val authStateCallback = client.sendWithCallback(
            TdApi.GetAuthorizationState(),
            object : ITdQueryHandler<TgClientAuthorizationStateEnum, TdApi.Error> {
                override fun onResult(obj: TdApi.Object): TgClientAuthorizationStateEnum {
                        if (obj is TdApi.AuthorizationStateReady) {
                            return TgClientAuthorizationStateEnum.READY
                        }
                    return TgClientAuthorizationStateEnum.UNDEFINED
                }
                override fun onError(error: TdApi.Error): TdApi.Error {
                    return error
                }
            }
        )

        when (authStateCallback) {
            is ErrorTdQueryHandlerResponse -> return ResponseEntity
                                                    .status(authStateCallback.error.code)
                                                    .body(authStateCallback.error.message)
            is SuccessTdQueryHandlerResponse ->
                if (authStateCallback.result == TgClientAuthorizationStateEnum.READY)
                    return ResponseEntity.ok("already authorized")
        }

        val checkAuthCodeCallback = client.sendWithCallback(TdApi.CheckAuthenticationCode(loginCode))
        when (checkAuthCodeCallback) {
            is SuccessTdQueryHandlerResponse -> if (checkAuthCodeCallback.result is TdApi.Ok)
                return ResponseEntity.ok("auth code received successfully")
            is ErrorTdQueryHandlerResponse -> return ResponseEntity
                .status(checkAuthCodeCallback.error.code)
                .body(checkAuthCodeCallback.error.message)
        }
        return ResponseEntity.status(400).body("error")
    }

}