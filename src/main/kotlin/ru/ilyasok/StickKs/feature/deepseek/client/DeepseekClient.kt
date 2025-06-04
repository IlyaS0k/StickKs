package ru.ilyasok.StickKs.feature.deepseek.client

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import org.springframework.ai.chat.messages.UserMessage
import org.springframework.ai.chat.model.ChatResponse
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.deepseek.DeepSeekChatModel
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service

@Service
@Profile("!test")
class DeepseekClient(private val chatModel: DeepSeekChatModel) {

    suspend fun ask(message: String): ChatResponse? {
        val prompt = Prompt(UserMessage(message));
        return chatModel.call(prompt)
    }

}