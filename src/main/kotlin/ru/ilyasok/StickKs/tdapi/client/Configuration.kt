package ru.ilyasok.StickKs.tdapi.client

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ru.ilyasok.StickKs.tdapi.utils.handlerprovider.abstraction.IHandlerByTdobjClass
import ru.ilyasok.StickKs.tdapi.utils.handlerprovider.implementation.HandlerByTdobjClass

@Configuration
class Configuration {

    @Bean
    fun handlerByTdobjClass(): IHandlerByTdobjClass = HandlerByTdobjClass.createWithoutDefaultHandler()
}