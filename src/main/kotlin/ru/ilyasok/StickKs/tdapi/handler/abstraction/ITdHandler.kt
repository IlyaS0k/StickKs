package ru.ilyasok.StickKs.tdapi.handler.abstraction

import ru.ilyasok.StickKs.tdapi.TdApi

interface ITdHandler {
    suspend fun handle(obj: TdApi.Object)
}
