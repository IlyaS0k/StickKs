package ru.ilyasok.StickKs.tdapi.model

class SuccessTdQueryHandlerResponse<R, E> constructor(val result: R) : TdQueryHandlerResponse<R, E>()