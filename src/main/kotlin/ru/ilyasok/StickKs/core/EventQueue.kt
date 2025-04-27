package ru.ilyasok.StickKs.core

import org.springframework.stereotype.Component
import ru.ilyasok.StickKs.core.event.Event
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.PriorityBlockingQueue

@Component
internal class EventQueue(
    private val queue: PriorityBlockingQueue<Event> = PriorityBlockingQueue(),
) {
    fun submit(event: Event) {
    }
}