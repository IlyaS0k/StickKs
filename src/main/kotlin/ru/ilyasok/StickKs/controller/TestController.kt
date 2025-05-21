package ru.ilyasok.StickKs.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import ru.ilyasok.StickKs.feature.deepseek.client.DeepseekClient


@Controller
@RequestMapping("/test")
class TestController(
    private val deepseekClient: DeepseekClient
) {

    @GetMapping()
    suspend fun test(): String? {
        deepseekClient.ask("привет, как дела").collect { println(it) }
        return "wstest"
    }
}