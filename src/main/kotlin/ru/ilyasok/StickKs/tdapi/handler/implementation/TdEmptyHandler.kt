package ru.ilyasok.StickKs.tdapi.handler.implementation

import ru.ilyasok.StickKs.tdapi.TdApi
import ru.ilyasok.StickKs.tdapi.client.abstraction.ITgClient
import ru.ilyasok.StickKs.tdapi.handler.abstraction.ITdHandler

class TdEmptyHandler : ITdHandler {
    override fun handle(client: ITgClient, obj: TdApi.Object) {}
}