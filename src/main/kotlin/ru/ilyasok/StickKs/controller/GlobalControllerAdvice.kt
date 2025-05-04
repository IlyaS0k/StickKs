package ru.ilyasok.StickKs.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import ru.ilyasok.StickKs.controller.exception.InvalidFeatureException


@ControllerAdvice
class GlobalControllerAdvice {

    @ExceptionHandler(InvalidFeatureException::class)
    @ResponseBody
    fun handleResponseStatus(ex: InvalidFeatureException): ResponseEntity<MutableMap<String?, String?>?> {
        val body: MutableMap<String?, String?> = HashMap()
        body.put("reason", ex.message)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body<MutableMap<String?, String?>?>(body)
    }
}