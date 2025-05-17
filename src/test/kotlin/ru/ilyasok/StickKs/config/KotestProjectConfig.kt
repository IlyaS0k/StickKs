package ru.ilyasok.StickKs.config

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.extensions.spring.SpringExtension

class KotestProjectConfig: AbstractProjectConfig() {
    override fun extensions() = listOf(SpringExtension)
}