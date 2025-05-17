package ru.ilyasok.StickKs.service

import org.springframework.dao.OptimisticLockingFailureException

suspend fun <T> optimisticTry(maxAttempts: Long = Long.MAX_VALUE, block: suspend () -> T): T {
    val attempts = 0
    while (attempts < maxAttempts) {
        try {
            return block()
        } catch (_: OptimisticLockingFailureException) {
            attempts.inc()
        }
    }
    throw RuntimeException("Max optimistic retry attempts exceeded")
}