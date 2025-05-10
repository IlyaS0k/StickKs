package ru.ilyasok.StickKs.tdapi.model.response

sealed class TdQueryResult<R, E> {

    companion object {
        fun <R, E> success(result: R): TdQueryResult<R, E> {
            return SuccessTdQueryResult(result)
        }

        fun <R, E> error(error: E): TdQueryResult<R, E> {
            return ErrorTdQueryResult(error)
        }
    }

    suspend fun<T> handle(buildHandlers: TdQueryResultBuilder<R, E, T>.() -> Unit): T? {
        val handlers = TdQueryResultBuilder<R, E, T>().apply(buildHandlers).build()
        return when (this) {
            is SuccessTdQueryResult -> handlers.onSuccess(result)
            is ErrorTdQueryResult -> handlers.onError(error)
        }
    }
}