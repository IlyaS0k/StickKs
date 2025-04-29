package ru.ilyasok.StickKs.controller.abstraction

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestBody

interface IAuthorizationController {
    suspend fun authByPhone(): String
    suspend fun submitPhone(@RequestBody phoneNumber: String?): ResponseEntity<String>
    suspend fun submitLoginCode(@RequestBody loginCode: String?): ResponseEntity<String>
}