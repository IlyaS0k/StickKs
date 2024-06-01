package ru.ilyasok.StickKs.tdapi.handler.implementation;

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component
import ru.ilyasok.StickKs.tdapi.TdApi
import ru.ilyasok.StickKs.tdapi.client.abstraction.ITgClient
import ru.ilyasok.StickKs.tdapi.handler.abstraction.ITdMainHandler
import ru.ilyasok.StickKs.tdapi.utils.TdEqRelation
import ru.ilyasok.StickKs.tdapi.utils.TdEquals
import ru.ilyasok.StickKs.tdapi.utils.handlerprovider.abstraction.IHandlerByTdobjClass

@Component
class TdMainHandler(
    override val handlersByTdobj: IHandlerByTdobjClass
): ITdMainHandler {

    @Lazy
    @Autowired
    override lateinit var client: ITgClient

    override fun onResult(obj: TdApi.Object) {
        val handler = handlersByTdobj.getOrDefault(obj.javaClass)
        when (TdEqRelation.TD_EQUALS) {
             TdEquals.check(obj, TdApi.UpdateAuthorizationState::class.java) -> handler.handle(client, obj)
             else -> return
        }

    }


}
