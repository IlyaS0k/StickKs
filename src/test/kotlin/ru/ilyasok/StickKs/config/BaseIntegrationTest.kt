package ru.ilyasok.StickKs.config

import io.kotest.core.spec.style.StringSpec
import org.junit.jupiter.api.Disabled
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import

@SpringBootTest
@Disabled
@Import(TestTgConfiguration::class, PostgresContainerConfig::class)
class BaseIntegrationTest(block: StringSpec.() -> Unit) : StringSpec(block)