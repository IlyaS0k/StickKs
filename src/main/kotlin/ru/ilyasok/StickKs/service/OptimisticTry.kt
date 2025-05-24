package ru.ilyasok.StickKs.service

import kotlinx.coroutines.delay
import org.springframework.dao.OptimisticLockingFailureException

suspend fun <T> optimisticTry(maxAttempts: Long = Long.MAX_VALUE, block: suspend () -> T): T {
    val attempts = 0
    val delay = 100L
    while (attempts < maxAttempts) {
        try {
            return block()
        } catch (_: OptimisticLockingFailureException) {
            delay(delay)
            delay.plus(100L)
            attempts.inc()
        }
    }
    throw RuntimeException("Max optimistic retry attempts exceeded")
}