package ru.ilyasok.StickKs.dsl

class AlwaysAvailableConfigurationBlock(): ConfigurationBlock()

@FeatureDSL
fun FeatureBlockBuilder.alwaysAvailable() {
    this.configuration = AlwaysAvailableConfigurationBlock()
}