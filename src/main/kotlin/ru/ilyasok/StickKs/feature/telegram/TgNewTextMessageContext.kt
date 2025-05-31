package ru.ilyasok.StickKs.feature.telegram

import ru.ilyasok.StickKs.dsl.FeatureDslComponent
import ru.ilyasok.StickKs.tdapi.TdApi

@FeatureDslComponent
data class TgNewTextMessageContext(
    val id: Long,
    val chatId: Long,
    val text: String = "",
    val sender: TdApi.User = TdApi.User(),
) : TgEventContext()