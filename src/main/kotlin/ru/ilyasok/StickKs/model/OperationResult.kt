package ru.ilyasok.StickKs.model

open class OperationResult(
    val success: Boolean,
    val error: Throwable? = null
) {
    init {
        require((success && error == null) || !success) {
            "Failed to construct OperationResult"
        }
    }
}