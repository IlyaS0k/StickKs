package ru.ilyasok.StickKs.feature.deepseek.client

import org.springframework.ai.chat.messages.UserMessage
import org.springframework.ai.chat.model.ChatResponse
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.deepseek.DeepSeekChatModel
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service

@Service
@Profile("!test")
@ConditionalOnProperty(name = ["SPRING_AI_DEEPSEEK_API_KEY"])
class DeepseekClient(private val chatModel: DeepSeekChatModel) {

    suspend fun ask(message: String): ChatResponse? {
        val prompt = Prompt(UserMessage(message));
        return chatModel.call(prompt)
    }

}