package ru.ilyasok.StickKs.core

import org.springframework.stereotype.Component
import ru.ilyasok.StickKs.dsl.FeatureBlock

@Component
class FeatureManager {
    private val active: MutableList<FeatureBlock> = mutableListOf()
    private val nonActive: MutableList<FeatureBlock> = mutableListOf()

    fun addAll(features: List<FeatureBlock>) {
        nonActive.addAll(features)
    }

    fun add(feature: FeatureBlock) {
        nonActive.add(feature)
    }

    fun getFeatures(): List<FeatureBlock> {

        return active
    }
}