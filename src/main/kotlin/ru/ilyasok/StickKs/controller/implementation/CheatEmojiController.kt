package ru.ilyasok.StickKs.controller.implementation;

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.ilyasok.StickKs.tdapi.TdApi
import ru.ilyasok.StickKs.tdapi.client.abstraction.ITgClient
import ru.ilyasok.StickKs.tdapi.handler.abstraction.ITdQueryHandler
import ru.ilyasok.StickKs.tdapi.model.response.SuccessTdQueryHandlerResponse
import ru.ilyasok.StickKs.tdapi.utils.DiceEmojiMaxValueProvider

@RestController
@RequestMapping("/cheat-emoji")
class CheatEmojiController @Autowired constructor(
    val client: ITgClient,
) {

    @GetMapping
    suspend fun cheatEmoji(
        @RequestParam("emojiName") emojiName: String,
        @RequestParam("times") times: Int,
        @RequestParam("chatName") chatName: String
    ) {
        val chatsIdsResponse = client.sendWithCallback(TdApi.GetChats(TdApi.ChatListMain(), 20),
            object : ITdQueryHandler<LongArray, TdApi.Error> {
                override fun onResult(obj: TdApi.Object): LongArray {
                    val chats = obj as TdApi.Chats
                    return chats.chatIds
                }

                override fun onError(error: TdApi.Error): TdApi.Error {
                    return error
                }
            }
        )
        val queriedChat: TdApi.Chat? = if (chatsIdsResponse is SuccessTdQueryHandlerResponse) {
            var resultChat: TdApi.Chat? = null
            for (id in chatsIdsResponse.result) {
                val chatResponse = client.sendWithCallback(TdApi.GetChat(id))
                if (chatResponse is SuccessTdQueryHandlerResponse &&
                    chatResponse.result is TdApi.Chat &&
                    chatResponse.result.title.equals(chatName)
                ) {
                    resultChat = chatResponse.result
                }
            }
            resultChat
        } else return
        if (queriedChat != null) {
            sendEmojisManyTimes(emojiName, times, queriedChat)
        }
    }

    private suspend fun sendEmojisManyTimes(emojiName: String, times: Int, queriedChat: TdApi.Chat) = coroutineScope {
        val messageDice = TdApi.InputMessageDice(emojiName, true)
        for (sending in 1..times) {
            launch {
                val messageDiceResponse = client.sendWithCallback(
                    TdApi.SendMessage(queriedChat.id, 0, null, null, null, messageDice)
                )
                if (messageDiceResponse is SuccessTdQueryHandlerResponse) {
                    val receivedMsg = messageDiceResponse.result as TdApi.Message
                    val msgFinalUpdate = client.getUpdateMessageContentEventAsync(receivedMsg.id)
                    val newContent = msgFinalUpdate!!.newContent as TdApi.MessageDice
                    if (newContent.value != DiceEmojiMaxValueProvider.provideFor(emojiName)) {
                        val res = client.sendWithCallback(
                            TdApi.DeleteMessages(queriedChat.id, longArrayOf(receivedMsg.id),true)
                        )
                    }
                }
            }
        }
    }

}
