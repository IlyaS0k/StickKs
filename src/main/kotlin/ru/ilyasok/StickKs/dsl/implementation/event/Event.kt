package ru.ilyasok.StickKs.dsl.implementation.event

import ru.ilyasok.StickKs.dsl.implementation.context.Context
import ru.ilyasok.StickKs.dsl.implementation.context.TgEventInitStrategy

sealed class Event {

    fun fillContext(): Context = when (this) {
            is TgEvent -> TgEventInitStrategy().execute(this)
            is UserEvent -> TODO()
        }
}