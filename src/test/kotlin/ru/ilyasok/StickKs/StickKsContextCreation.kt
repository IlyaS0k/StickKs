package ru.ilyasok.StickKs

import io.kotest.core.spec.style.StringSpec
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import ru.ilyasok.StickKs.config.TestTgConfiguration

@SpringBootTest
@Import(TestTgConfiguration::class)
class StickKsContextCreation : StringSpec()
