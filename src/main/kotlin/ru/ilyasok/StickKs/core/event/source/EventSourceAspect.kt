package ru.ilyasok.StickKs.core.event.source

import org.aspectj.lang.annotation.AfterReturning
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.springframework.stereotype.Component
import ru.ilyasok.StickKs.core.context.EventContext
import ru.ilyasok.StickKs.core.event.queue.EventQueue

@Aspect
@Component
class EventSourceAspect(
    private val eventQueue: EventQueue
) {

    @Pointcut("execution(* ru.ilyasok.StickKs.core.event.source.IEventSource.publishEvent(..))")
    fun publishEventPointcut() {}

    @AfterReturning(value = "publishEventPointcut()", returning = "event")
    fun afterReturningPublishEventAdvice(event: EventContext?) {
        if (event != null) {
            eventQueue.enqueue(event)
        }
    }
}