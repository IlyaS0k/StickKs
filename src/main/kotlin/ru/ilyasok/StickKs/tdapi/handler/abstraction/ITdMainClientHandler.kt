package ru.ilyasok.StickKs.tdapi.handler.abstraction

import ru.ilyasok.StickKs.tdapi.Client
import ru.ilyasok.StickKs.tdapi.client.abstraction.ITgClient


interface ITdMainClientHandler: Client.ResultHandler {
    fun forClient(client: ITgClient)
}