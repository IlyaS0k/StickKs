package ru.ilyasok.StickKs.dsl

class AlwaysAvailableAvailabilityBlock(): AvailabilityBlock() {
    override fun isAvailable(meta: FeatureMeta): Boolean = true
}

@FeatureDSL
fun FeatureBlockBuilder.alwaysAvailable() {
    this.availability = AlwaysAvailableAvailabilityBlock()
}