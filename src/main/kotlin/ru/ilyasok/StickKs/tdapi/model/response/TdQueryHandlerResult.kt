package ru.ilyasok.StickKs.tdapi.model.response

sealed class TdQueryHandlerResult<R, E> {

    companion object {
        fun <R, E> success(result: R): TdQueryHandlerResult<R, E> {
            return SuccessTdQueryHandlerResult(result)
        }

        fun <R, E> error(error: E): TdQueryHandlerResult<R, E> {
            return ErrorTdQueryHandlerResult(error)
        }
    }

    fun<T> handle(buildHandlers: TdQueryHandleResultBuilder<R, E, T>.() -> Unit): T? {
        val handlers = TdQueryHandleResultBuilder<R, E, T>().apply(buildHandlers).build()
        return when (this) {
            is SuccessTdQueryHandlerResult -> handlers.onSuccess(result)
            is ErrorTdQueryHandlerResult -> handlers.onError(error)
        }
    }
}