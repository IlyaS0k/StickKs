package ru.ilyasok.StickKs.tdapi.model.response

class ErrorTdQueryResult<R, E> constructor(val error: E) : TdQueryResult<R, E>()