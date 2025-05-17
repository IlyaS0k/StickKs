package ru.ilyasok.StickKs.feature.telegram

import ru.ilyasok.StickKs.dsl.FeatureDSL
import ru.ilyasok.StickKs.tdapi.TdApi

@FeatureDSL
data class TgNewTextMessageContext(
    val id: Long,
    val chatId: Long,
    val text: String = "",
    val sender: TdApi.User = TdApi.User(),
) : TgEventContext()