package ru.ilyasok.StickKs.tdapi.utils

class DiceEmojiMaxValueProvider {
    companion object {
        fun provideFor(emoji: String): Int? {
            return when (emoji) {
                "\uD83C\uDFB2" -> 6
                "\uD83C\uDFAF" -> 6
                "\uD83C\uDFB3" -> 6
                "\uD83C\uDFB0" -> 64
                "\uD83C\uDFC0" -> 5
                "⚽" -> 5
                else -> null
            }
        }
    }
}