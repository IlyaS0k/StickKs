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

    fun isSuccess(): Boolean {
        return this is SuccessTdQueryHandlerResponse<R, E>
    }

    fun isError(): Boolean {
        return this is ErrorTdQueryHandlerResponse<R, E>
    }

}