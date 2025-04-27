package ru.ilyasok.StickKs.core

import org.aspectj.lang.annotation.AfterReturning
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.springframework.stereotype.Component
import ru.ilyasok.StickKs.core.event.Event
import ru.ilyasok.StickKs.tdapi.feature.event.TgReceiveTextMessageEvent

@Aspect
@Component
class EventSourceAspect {

    @Pointcut("execution(* ru.ilyasok.StickKs.core.event.source.IEventSource.publishEvent(..))")
    fun publishEventPointcut() {
    }

    @AfterReturning(value = "publishEventPointcut()", returning = "event")
    fun afterReturningPublishEventAdvice(event: Event?) {
        if (event != null) {
            println("event message: ${(event as TgReceiveTextMessageEvent).context.message} ")
        }
    }
}