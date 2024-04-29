package ru.ilyasok.StickKs.controller.implementation

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import ru.ilyasok.StickKs.controller.abstraction.ITgClientAuthorizationController
import ru.ilyasok.StickKs.tdapi.TdApi
import ru.ilyasok.StickKs.tdapi.client.abstraction.ITgClient


@Controller
@RequestMapping("/auth")
class TgClientAuthorizationController @Autowired constructor(val client: ITgClient) : ITgClientAuthorizationController {

    val baseUrl = "/auth"

    @GetMapping("/by-phone")
    fun authByPhone(): String {

         return "authorization-by-phone"
    }

   @PostMapping("/submit-phone")
   @ResponseBody
   fun submitPhone(@RequestBody phoneNumber: String?): ResponseEntity<String> {
       client.send(TdApi.SetAuthenticationPhoneNumber(phoneNumber, null))
       return ResponseEntity.ok("phone-response")
   }

    @PostMapping("/submit-login-code")
    @ResponseBody
    fun submitLoginCode(@RequestBody loginCode: String?): ResponseEntity<String> {
        client.send(TdApi.CheckAuthenticationCode(loginCode))
        return ResponseEntity.ok("code-response")
    }

}