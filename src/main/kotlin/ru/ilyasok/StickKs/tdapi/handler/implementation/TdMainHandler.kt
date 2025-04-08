package ru.ilyasok.StickKs.tdapi.handler.implementation;

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component
import ru.ilyasok.StickKs.tdapi.TdApi
import ru.ilyasok.StickKs.tdapi.TdApi.UpdateAuthorizationState
import ru.ilyasok.StickKs.tdapi.client.abstraction.ITgClient
import ru.ilyasok.StickKs.tdapi.handler.abstraction.ITdHandler
import ru.ilyasok.StickKs.tdapi.handler.abstraction.ITdMainHandler
import ru.ilyasok.StickKs.tdapi.utils.TdEqRelation
import ru.ilyasok.StickKs.tdapi.utils.TdEquals

@Component
class TdMainHandler(
    private val handlers: List<ITdHandler>,
): ITdMainHandler {

    @Lazy
    @Autowired
    override lateinit var client: ITgClient

    override fun onResult(obj: TdApi.Object) {
        for (handler in handlers) {
           handler.handle(client, obj)
        }
    }

}
