package ru.ilyasok.StickKs.tdapi.feature

import ru.ilyasok.StickKs.core.context.EventContext
import ru.ilyasok.StickKs.tdapi.TdApi

class TgNewTextMessageContext(
    val messageText: String = "",
    val sender: TdApi.User = TdApi.User(),
) : EventContext()