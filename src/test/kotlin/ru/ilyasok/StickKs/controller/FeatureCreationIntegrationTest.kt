package ru.ilyasok.StickKs.controller

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.equals.shouldBeEqual
import org.springframework.transaction.annotation.Transactional
import ru.ilyasok.StickKs.config.BaseIntegrationTest
import ru.ilyasok.StickKs.dsl.DSLDependenciesProvider
import ru.ilyasok.StickKs.exception.FeatureOperationException
import ru.ilyasok.StickKs.model.SaveFeatureRequest
import java.util.*

class FeatureCreationIntegrationTest(
    private val controller: FeatureController
) : BaseIntegrationTest({


    "creates feature" {
        DSLDependenciesProvider.findDependencies()
        val featureSourceCode = """
            feature {
                name = "delete msg"
                withTimeout {        afterStart = 0.minutes
                    timeout = 60.minutes    }
                onEvent {
                    newTelegramMessage {
                        withCondition { message ->                listOf("ilyaS0k", "natalia_sharshakova").contains(message.sender.usernames.editableUsername)
                        }
                        execute { message ->                sendMessageTo(user { username = "k1ssik" }) { "Привет" }
                        }        }
            }}
        """.trimIndent()
        val result = controller.save(UUID.randomUUID(), SaveFeatureRequest(
            id = null,
            code = featureSourceCode
        ))
        result.code.shouldBeEqual(featureSourceCode)
    }

    "incorrect feature fails" {
        val feature = "{ I AM NOT CORRECT }"
        shouldThrow<FeatureOperationException> {
            controller.save(UUID.randomUUID(), SaveFeatureRequest(
                id = null,
                code = feature
            ))
        }
    }
})
