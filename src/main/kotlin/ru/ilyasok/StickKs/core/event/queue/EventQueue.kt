package ru.ilyasok.StickKs.core.event.queue

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import ru.ilyasok.StickKs.core.context.EventContext

@Component
class EventQueue {
    private val queue: Channel<EventContext> = Channel(Channel.UNLIMITED)

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java)
    }


    fun enqueue(e: EventContext) = runBlocking {
        queue.send(e)
        logger.info("Added new event ${System.identityHashCode(e)}")
    }

    suspend fun dequeue(): EventContext {
        return queue.receive()
    }
}