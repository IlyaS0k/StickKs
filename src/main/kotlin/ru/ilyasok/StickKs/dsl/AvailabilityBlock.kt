package ru.ilyasok.StickKs.dsl

abstract class AvailabilityBlock {
    abstract fun isAvailable(meta: FeatureMeta): Boolean
}