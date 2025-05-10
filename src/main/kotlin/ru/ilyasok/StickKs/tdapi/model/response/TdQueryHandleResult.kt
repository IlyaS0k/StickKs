package ru.ilyasok.StickKs.tdapi.model.response

class TdQueryResultBuilder<R, E, T> {
    var onSuccess: suspend (R) -> T? = { null }
    var onError: suspend (E) -> T? = { null }

    fun build(): TdQueryOnResult<R, E, T> = TdQueryOnResult(onSuccess, onError)
}

class TdQueryOnResult<R, E, T>(
    val onSuccess: suspend (R) -> T?,
    val onError: suspend (E) -> T?
)