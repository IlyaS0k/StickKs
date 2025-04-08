package ru.ilyasok.StickKs.dsl.abstraction

import ru.ilyasok.StickKs.dsl.implementation.context.Context
import ru.ilyasok.StickKs.dsl.implementation.event.Event

interface IContextInitStrategy<T: Event> {
    fun execute(obj: T): Context
}