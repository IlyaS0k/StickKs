package ru.ilyasok.StickKs.tdapi.feature.context

import ru.ilyasok.StickKs.core.context.EventContext
import ru.ilyasok.StickKs.tdapi.TdApi

class TgNewTextMessageContext(
    val message: String = "",
    val user: TdApi.User? = null,
) : EventContext()