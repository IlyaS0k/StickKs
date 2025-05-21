package ru.ilyasok.StickKs.dsl

abstract class ExecutionControlBlock {
    abstract fun control(meta: FeatureMeta): Boolean
}