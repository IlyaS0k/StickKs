package ru.ilyasok.StickKs.core.event.queue

import jakarta.annotation.PostConstruct
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import ru.ilyasok.StickKs.core.context.EventContext
import ru.ilyasok.StickKs.dsl.FeatureProcessor

@Component
class EventQueue(
    private val featureProcessor: FeatureProcessor
) {
    private val queue: Channel<EventContext> = Channel(Channel.Factory.UNLIMITED)

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java)
    }

    @PostConstruct
    fun postConstruct() = runBlocking {
        CoroutineScope(Dispatchers.Default + CoroutineName("EventQueueCoro")).launch {
            loop()
        }
    }

    fun enqueue(e: EventContext) = runBlocking {
        queue.send(e)
        logger.info("added new event ${System.identityHashCode(e)}")
    }

    private suspend fun loop() = coroutineScope {
        while (true) {
            val ec = queue.receive()
            logger.info("received event ${ec.hashCode()}")
            try {
                featureProcessor.process(ec)
            } catch (_: Throwable) {

            }
        }
    }
}