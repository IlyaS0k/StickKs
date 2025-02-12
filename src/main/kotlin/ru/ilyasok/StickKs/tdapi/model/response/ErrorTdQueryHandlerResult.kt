package ru.ilyasok.StickKs.tdapi.model.response

class ErrorTdQueryHandlerResult<R, E> constructor(val error: E) : TdQueryHandlerResult<R, E>()