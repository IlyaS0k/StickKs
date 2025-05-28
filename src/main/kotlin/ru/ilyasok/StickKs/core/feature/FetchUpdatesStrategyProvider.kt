package ru.ilyasok.StickKs.core.feature

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class FetchUpdatesStrategyProvider(
    @param:Value("\${stickks.features.management.fetch-updates-strategy}")
    private val fetchUpdatesStrategy: String,
) {

    companion object {
        private const val DEFAULT_STRATEGY = "DEFAULT"
    }

    fun provideFor(featureManager: FeatureManager): IFetchUpdatesStrategy = when (fetchUpdatesStrategy) {
        DEFAULT_STRATEGY -> DefaultFetchUpdatesStrategy(featureManager)

        else -> throw Error("Unknown fetch updates strategy: $fetchUpdatesStrategy")
    }
}