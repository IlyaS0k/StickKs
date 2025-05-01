package ru.ilyasok.StickKs.core.event.source

import ru.ilyasok.StickKs.core.context.EventContext


interface IEventSource {

    fun publishEvent(e: EventContext?) = e
}