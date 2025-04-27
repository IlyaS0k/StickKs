package ru.ilyasok.StickKs.tdapi.feature.event

import ru.ilyasok.StickKs.core.event.Event
import ru.ilyasok.StickKs.tdapi.feature.context.TgNewTextMessageContext

class TgReceiveTextMessageEvent(val context: TgNewTextMessageContext) : Event()
