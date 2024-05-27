package ru.ilyasok.StickKs.tdapi.model

class ErrorTdQueryHandlerResponse<R, E> constructor(val error: E) : TdQueryHandlerResponse<R, E>()