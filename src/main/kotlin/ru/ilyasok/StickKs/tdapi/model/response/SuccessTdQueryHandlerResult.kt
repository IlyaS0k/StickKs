package ru.ilyasok.StickKs.tdapi.model.response

class SuccessTdQueryHandlerResult<R, E> constructor(val result: R) : TdQueryHandlerResult<R, E>()