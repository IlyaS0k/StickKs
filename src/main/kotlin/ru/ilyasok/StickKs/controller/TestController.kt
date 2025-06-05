package ru.ilyasok.StickKs.controller

import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping


@Controller
@RequestMapping("/test")
@Profile("!test")
class TestController() {

    @GetMapping()
    suspend fun test(): Unit {

        return
    }
}