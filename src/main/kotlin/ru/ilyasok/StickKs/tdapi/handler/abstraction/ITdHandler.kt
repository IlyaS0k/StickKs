package ru.ilyasok.StickKs.tdapi.handler.abstraction

import ru.ilyasok.StickKs.tdapi.TdApi
import ru.ilyasok.StickKs.tdapi.client.abstraction.ITgClient

interface ITdHandler {
    fun handle(client: ITgClient, obj: TdApi.Object)
}
