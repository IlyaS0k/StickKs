package ru.ilyasok.StickKs.mocks

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service
import ru.ilyasok.StickKs.core.context.EventContext
import ru.ilyasok.StickKs.core.event.source.IEventSource
import ru.ilyasok.StickKs.tdapi.handler.implementation.TdFeatureHandler

@Service
class ManualEventSource : IEventSource {

    @Autowired
    @Lazy
    private lateinit var self: ManualEventSource

    fun manuallyPublishEvent(eventContext: EventContext) {
            self.publishEvent(eventContext)
    }
}