package ru.ilyasok.StickKs.core

import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Component
import ru.ilyasok.StickKs.core.context.EventContext
import ru.ilyasok.StickKs.dsl.FeatureProcessor

@Component
class EventQueue(
    private val featureProcessor: FeatureProcessor
) {
    private val queue: Channel<EventContext> = Channel(Channel.UNLIMITED)

    @PostConstruct
    fun postConstruct() = runBlocking {
        CoroutineScope(Dispatchers.Default).launch {
            loop()
        }
    }

    @PreDestroy
    fun preDestroy() {
        queue.close()
    }

    fun enqueue(e: EventContext) = runBlocking {
        queue.send(e)
    }

    private suspend fun loop() = coroutineScope {
        while (true) {
            val ec = queue.receive()
            launch {
                featureProcessor.process(ec)
            }
        }
    }
}