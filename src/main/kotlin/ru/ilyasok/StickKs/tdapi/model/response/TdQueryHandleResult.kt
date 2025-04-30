package ru.ilyasok.StickKs.tdapi.model.response

class TdQueryesultBuilder<R, E, T> {
    var onSuccess: (R) -> T? = { null }
    var onError: (E) -> T? = { null }

    fun build(): TdQueryesult<R, E, T> = TdQueryesult(onSuccess, onError)
}

class TdQueryesult<R, E, T>(
    val onSuccess: (R) -> T?,
    val onError: (E) -> T?
)