package ru.ilyasok.StickKs.tdapi.model.response

class SuccessTdQueryResult<R, E> constructor(val result: R) : TdQueryResult<R, E>()