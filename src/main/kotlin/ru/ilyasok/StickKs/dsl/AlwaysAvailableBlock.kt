package ru.ilyasok.StickKs.dsl

class AlwaysAvailableBlock(private val limit: Long): ExecutionControlBlock() {
    override fun control(meta: FeatureMeta): Boolean = meta.successExecutionsAmount < limit
}

@FeatureDSL
fun FeatureBlockBuilder.always(limit: () -> Long = { Long.MAX_VALUE }) {
    this.executionControl = AlwaysAvailableBlock(limit.invoke())
}