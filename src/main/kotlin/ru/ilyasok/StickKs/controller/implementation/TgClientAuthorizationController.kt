package ru.ilyasok.StickKs.controller.implementation

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import ru.ilyasok.StickKs.controller.abstraction.ITgClientAuthorizationController
import ru.ilyasok.StickKs.tdapi.TdApi
import ru.ilyasok.StickKs.tdapi.client.TgClientAuthorizationState
import ru.ilyasok.StickKs.tdapi.client.abstraction.ITgClient


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
        client.sendWithCallback(TdApi.SetAuthenticationPhoneNumber(phoneNumber, null)).handle {
            onSuccess = {
                ResponseEntity.ok("The phone number has been set")
            }
            onError = { err ->
                ResponseEntity.status(err.code)
                .body(err.message)
            }
        }?.let { return it }

        return ResponseEntity.status(400).body("error")
    }

    @PostMapping("/submit-login-code")
    @ResponseBody
    override suspend fun submitLoginCode(@RequestBody loginCode: String?): ResponseEntity<String> {
        client.getAuthorizationState().handle {
            onSuccess = { res ->
                if (res == TgClientAuthorizationState.READY) {
                    ResponseEntity.ok("already authorized")
                } else null
            }
            onError = { err ->
                ResponseEntity
                    .status(err.code)
                    .body(err.message)
            }
        }?.let { return it }

        client.sendWithCallback(TdApi.CheckAuthenticationCode(loginCode)).handle {
            onSuccess = { res ->
                ResponseEntity.ok("auth code received successfully")
            }
            onError = { err ->
                ResponseEntity
                    .status(err.code)
                    .body(err.message)
            }
        }?.let { return it }

        return ResponseEntity.status(400).body("error")
    }
}