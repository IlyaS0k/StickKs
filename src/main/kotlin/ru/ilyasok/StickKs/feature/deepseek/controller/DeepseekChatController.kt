package ru.ilyasok.StickKs.feature.deepseek.controller

import org.springframework.ai.chat.messages.UserMessage
import org.springframework.ai.chat.model.ChatResponse
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.deepseek.DeepSeekChatModel
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import java.util.Map


@RestController("deepseek")
class ChatController(private val chatModel: DeepSeekChatModel) {
    @GetMapping("/ai/generate")
    suspend fun generate(@RequestParam(value = "message") message: String?): MutableMap<*, *> {
        return Map.of<String?, String?>("generation", chatModel.call(message))
    }

    @GetMapping("/ai/generateStream")
    suspend fun generateStream(
        @RequestParam(
            value = "message",
            defaultValue = "Tell me a joke"
        ) message: String
    ): Flux<ChatResponse?>? {
        val prompt = Prompt(UserMessage(message))
        return chatModel.stream(prompt)
    }
}
