package ru.ilyasok.StickKs.tdapi.model.response

sealed class TdQueryHandlerResponse<R, E> {

    companion object {
        fun <R, E> success(result: R): TdQueryHandlerResponse<R, E> {
            return SuccessTdQueryHandlerResponse(result)
        }

        fun <R, E> error(error: E): TdQueryHandlerResponse<R, E> {
            return ErrorTdQueryHandlerResponse(error)
        }
    }

    fun<T> handle(buildHandlers: TdQueryHandleResultBuilder<R, E, T>.() -> Unit): T? {
        val handlers = TdQueryHandleResultBuilder<R, E, T>().apply(buildHandlers).build()
        return when (this) {
            is SuccessTdQueryHandlerResponse -> handlers.onSuccess(result)
            is ErrorTdQueryHandlerResponse -> handlers.onError(error)
        }
    }
}