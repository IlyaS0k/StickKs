package ru.ilyasok.StickKs.tdapi.model.response

class SuccessTdQueryHandlerResponse<R, E> constructor(val result: R) : TdQueryHandlerResponse<R, E>()