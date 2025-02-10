package ru.ilyasok.StickKs.tdapi.model.response

class TdQueryHandleResultBuilder<R, E, T> {
    var onSuccess: (R) -> T? = { null }
    var onError: (E) -> T? = { null }

    fun build(): TdQueryHandleResult<R, E, T> = TdQueryHandleResult(onSuccess, onError)
}

class TdQueryHandleResult<R, E, T>(
    val onSuccess: (R) -> T?,
    val onError: (E) -> T?
)