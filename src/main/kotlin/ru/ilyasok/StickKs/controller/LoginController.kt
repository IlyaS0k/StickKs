package ru.ilyasok.StickKs.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import ru.ilyasok.StickKs.model.SubmitLoginCodeRequest
import ru.ilyasok.StickKs.model.SubmitPhoneRequest
import ru.ilyasok.StickKs.tdapi.client.TgClientAuthorizationState
import ru.ilyasok.StickKs.tdapi.client.abstraction.ITgClient

@Controller
class LoginController(val client: ITgClient) {


    @GetMapping("/login")
    suspend fun login(): String {
        return if (isAuthorized()) "redirect:/features" else "login"
    }

    @PostMapping("/logout")
    suspend fun logout(): ResponseEntity<String> {
        return client.logout().handle {
            onSuccess = { res ->
                ResponseEntity.ok().body("Logout success")
            }
            onError = { err ->
                ResponseEntity.badRequest().body(err.toString())
            }
        }!!
    }

    @PostMapping("/login/submit-phone")
    suspend fun submitPhone(@RequestBody req: SubmitPhoneRequest): ResponseEntity<String> {
        val phoneNumber = req.phone
        return phoneNumber.let {
            client.setPhoneNumber(phoneNumber).handle {
                onSuccess = { res ->
                    ResponseEntity.ok("The phone number has been set")
                }
                onError = { err ->
                    if (client.getAuthorizationState().handle { onSuccess = { it }  } == TgClientAuthorizationState.WAIT_CODE) {
                        ResponseEntity.ok("Waiting for code")
                    } else {
                        ResponseEntity.status(err.code).body(err.message)
                    }
                }
            }!!
        }
    }

    @PostMapping("/login/submit-login-code")
    suspend fun submitLoginCode(@RequestBody req: SubmitLoginCodeRequest): ResponseEntity<String> {
        val loginCode = req.loginCode
        return loginCode.let {
            client.checkAuthenticationCode(loginCode).handle {
                onSuccess = { res -> ResponseEntity.status(HttpStatus.OK).body("Login success") }
                onError = { err -> ResponseEntity.status(err.code).body(err.message) }
            }!!
        }
    }

    private suspend fun isAuthorized(): Boolean {
        return client.getAuthorizationState().handle { onSuccess = { it } } == TgClientAuthorizationState.READY
    }
}