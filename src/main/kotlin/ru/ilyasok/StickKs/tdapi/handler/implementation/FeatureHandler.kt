package ru.ilyasok.StickKs.tdapi.handler.implementation

import ru.ilyasok.StickKs.dsl.implementation.FeatureBlock
import ru.ilyasok.StickKs.tdapi.TdApi
import ru.ilyasok.StickKs.tdapi.client.abstraction.ITgClient
import ru.ilyasok.StickKs.tdapi.handler.abstraction.ITdHandler

class FeatureHandler : ITdHandler {

    var features: List<FeatureBlock> = listOf()

    override fun handle(client: ITgClient, obj: TdApi.Object) {

    }
}