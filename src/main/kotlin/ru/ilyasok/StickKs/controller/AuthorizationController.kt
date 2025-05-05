package ru.ilyasok.StickKs.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import ru.ilyasok.StickKs.tdapi.client.TgClientAuthorizationState
import ru.ilyasok.StickKs.tdapi.client.abstraction.ITgClient


@Controller
@RequestMapping("/auth")
class AuthorizationController @Autowired constructor(val client: ITgClient) {


    @GetMapping
    fun authByPhone(): String {
        return "auth"
    }

    @PostMapping("/submit-phone")
    @ResponseBody
    suspend fun submitPhone(@RequestBody phoneNumber: String?): ResponseEntity<String> {
        if (isAlreadyAuthorized()) return ResponseEntity("already authorized", HttpStatus.BAD_REQUEST)

        phoneNumber?.let {
            client.setPhoneNumber(phoneNumber).handle {
                onSuccess = {
                    ResponseEntity.ok("The phone number has been set")
                }
                onError = { err ->
                    ResponseEntity.status(err.code)
                        .body(err.message)
                }
            }?.let { return it }
        }

        return ResponseEntity.status(400).body("error")
    }

    @PostMapping("/submit-login-code")
    @ResponseBody
    suspend fun submitLoginCode(@RequestBody loginCode: String?): ResponseEntity<String> {
        if (isAlreadyAuthorized()) return ResponseEntity("already authorized", HttpStatus.BAD_REQUEST)

        loginCode?.let {
            client.checkAuthenticationCode(loginCode).handle {
                onSuccess = { res ->
                    ResponseEntity.ok("auth code received successfully")
                }
                onError = { err ->
                    ResponseEntity
                        .status(err.code)
                        .body(err.message)
                }
            }?.let { return it }
        }

        return ResponseEntity.status(400).body("error")
    }

    private suspend fun isAlreadyAuthorized(): Boolean {
       return client.getAuthorizationState().handle { onSuccess = { it } } == TgClientAuthorizationState.READY
    }
}