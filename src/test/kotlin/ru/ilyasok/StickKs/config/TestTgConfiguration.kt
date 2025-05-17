package ru.ilyasok.StickKs.config

import io.kotest.core.spec.style.StringSpec
import io.mockk.mockk
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import ru.ilyasok.StickKs.feature.telegram.functions.TgFunctions
import ru.ilyasok.StickKs.mocks.TestTgClient
import ru.ilyasok.StickKs.tdapi.client.abstraction.ITgClient

@TestConfiguration
class TestTgConfiguration {

    @Primary
    @Bean
    fun getTgClient(): ITgClient {
        return TestTgClient(mockk(), mockk())
    }

    @Bean
    @Primary
    fun getTgFunctions(): TgFunctions {
        return TgFunctions(getTgClient())
    }

    @Bean
    fun getDefault(): StringSpec.() -> Unit = {}
}