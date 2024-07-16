package ru.ilyasok.StickKs.tdapi.model.response

class ErrorTdQueryHandlerResponse<R, E> constructor(val error: E) : TdQueryHandlerResponse<R, E>()