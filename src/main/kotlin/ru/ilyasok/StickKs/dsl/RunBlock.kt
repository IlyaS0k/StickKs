package ru.ilyasok.StickKs.dsl

class RunBlock(
    val condition: suspend () -> Boolean,
    val execute: suspend () -> Unit,
) {
    suspend fun checkCondition(): Boolean = condition.invoke()

    suspend fun execute() = execute.invoke()
}