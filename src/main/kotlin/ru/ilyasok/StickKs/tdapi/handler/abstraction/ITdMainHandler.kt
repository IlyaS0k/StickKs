package ru.ilyasok.StickKs.tdapi.handler.abstraction

import ru.ilyasok.StickKs.tdapi.Client
import ru.ilyasok.StickKs.tdapi.client.abstraction.ITgClient
import ru.ilyasok.StickKs.tdapi.utils.handlerprovider.abstraction.IHandlerByTdobjClass


interface ITdMainHandler: Client.ResultHandler {
    val client: ITgClient
    val handlersByTdobj: IHandlerByTdobjClass
}