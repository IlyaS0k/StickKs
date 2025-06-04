package ru.ilyasok.StickKs.feature.deepseek.functions

import ru.ilyasok.StickKs.core.utils.SpringContext
import ru.ilyasok.StickKs.dsl.FeatureDslComponent
import ru.ilyasok.StickKs.feature.telegram.functions.TgFunctions

@FeatureDslComponent
suspend fun askDeepseek(prompt: () -> String): String {
    val tgFunctions = SpringContext.getBean(TgFunctions::class.java)
    if (prompt().isNotBlank()) {
        val deepseek = tgFunctions.deepseekClient
        return deepseek.ask(prompt())?.result?.output?.text ?: ""
    } else {
        throw RuntimeException("Empty deepseek message")
    }
}