package ru.ilyasok.StickKs.tdapi.utils.handlerprovider.abstraction

import ru.ilyasok.StickKs.tdapi.TdApi
import ru.ilyasok.StickKs.tdapi.handler.abstraction.ITdHandler

interface IHandlerByTdobjClass {
    val defaultHandler: ITdHandler
    fun add(tdClass: Class<out TdApi.Object>, handler: ITdHandler)
    fun remove(tdClass: Class<out TdApi.Object>)
    fun getOrDefault(tdClass: Class<out TdApi.Object>): ITdHandler
}