package ru.ilyasok.StickKs.tdapi.handler.abstraction

import ru.ilyasok.StickKs.tdapi.TdApi

interface ITdQuery<R, E> {

    fun onResult(obj: TdApi.Object) : R

    fun onError(error: TdApi.Error) : E
}