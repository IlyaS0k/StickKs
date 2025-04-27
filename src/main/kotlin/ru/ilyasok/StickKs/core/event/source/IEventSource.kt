package ru.ilyasok.StickKs.core.event.source

import ru.ilyasok.StickKs.core.event.Event

interface IEventSource {

    fun publishEvent(e: Event?) = e
}