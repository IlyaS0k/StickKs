package ru.ilyasok.StickKs.tdapi.utils.handlerprovider.implementation

import ru.ilyasok.StickKs.tdapi.TdApi
import ru.ilyasok.StickKs.tdapi.TdApi.UpdateAuthorizationState
import ru.ilyasok.StickKs.tdapi.handler.abstraction.ITdHandler
import ru.ilyasok.StickKs.tdapi.handler.implementation.TdAuthorizationHandler
import ru.ilyasok.StickKs.tdapi.handler.implementation.TdEmptyHandler
import ru.ilyasok.StickKs.tdapi.utils.handlerprovider.abstraction.IHandlerByTdobjClass
import java.util.concurrent.ConcurrentHashMap

class HandlerByTdobjClass private constructor() :
    IHandlerByTdobjClass {
    companion object {
        fun create(defaultHandler: ITdHandler): HandlerByTdobjClass {
            val defaultHandlersMap = mutableMapOf<Class<out TdApi.Object>, ITdHandler>(
                UpdateAuthorizationState::class.java to TdAuthorizationHandler()
            )
            return createByMap(defaultHandler, defaultHandlersMap)
        }

        fun createWithoutDefaultHandler(): HandlerByTdobjClass {
            return create(TdEmptyHandler())
        }

        fun createByMap(
            defaultHandler: ITdHandler,
            handlerByClass: MutableMap<Class<out TdApi.Object>, ITdHandler>
        ): HandlerByTdobjClass {
            return HandlerByTdobjClass(defaultHandler, handlerByClass)
        }
    }

    override var defaultHandler: ITdHandler = TdEmptyHandler()

    private val handlersByTdobjClassMap = ConcurrentHashMap<Class<out TdApi.Object>, ITdHandler>()

    private constructor(
        defaultHandler: ITdHandler,
        defaultHandlersMap: MutableMap<Class<out TdApi.Object>, ITdHandler>
    ) : this() {
        this.defaultHandler = defaultHandler
        handlersByTdobjClassMap.putAll(defaultHandlersMap)
    }

    override fun add(tdClass: Class<out TdApi.Object>, handler: ITdHandler) {
        handlersByTdobjClassMap[tdClass] = handler
    }

    override fun remove(tdClass: Class<out TdApi.Object>) {
        handlersByTdobjClassMap.remove(tdClass)
    }

    override fun getOrDefault(tdClass: Class<out TdApi.Object>): ITdHandler {
        return handlersByTdobjClassMap[tdClass] ?: defaultHandler
    }

}