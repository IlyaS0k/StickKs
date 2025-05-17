package ru.ilyasok.StickKs.tdapi.handler.implementation;

import kotlinx.coroutines.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Component
import ru.ilyasok.StickKs.tdapi.TdApi
import ru.ilyasok.StickKs.tdapi.handler.abstraction.ITdHandler
import ru.ilyasok.StickKs.tdapi.handler.abstraction.ITdMainHandler

@Component
class TdMainHandler(
    private val handlers: List<ITdHandler>,
) : ITdMainHandler {

    override fun onResult(obj: TdApi.Object): Unit = runBlocking {
        CoroutineScope(Dispatchers.Default + CoroutineName("TdMainHandlerCoro")).launch {
         for (handler in handlers) {
                handler.handle(obj)
            }
        }
    }

}
