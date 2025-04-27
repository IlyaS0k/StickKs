package ru.ilyasok.StickKs.core


import kotlinx.coroutines.*
import org.springframework.stereotype.Component
import ru.ilyasok.StickKs.core.event.Event
import ru.ilyasok.StickKs.dsl.implementation.FeatureBlock

@Component
internal class EventProcessor(
    private val eventQueue: Map<Class<Event>, List<Event>> = mapOf(),
    private val features: List<FeatureBlock> = listOf()
) {

    fun process() = runBlocking {
        while (true) {
            for (eventType in eventQueue.keys) {
                CoroutineScope(Dispatchers.IO).launch {

                }
            }
        }
    }
}