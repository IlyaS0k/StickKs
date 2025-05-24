package ru.ilyasok.StickKs.concurrency

import io.kotest.matchers.shouldBe
import io.mockk.mockk
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.ilyasok.StickKs.client.FeatureClient
import ru.ilyasok.StickKs.config.BaseIntegrationTest
import ru.ilyasok.StickKs.feature.telegram.TgNewTextMessageContext
import ru.ilyasok.StickKs.mocks.ManualEventSource
import ru.ilyasok.StickKs.model.SaveFeatureRequest
import ru.ilyasok.StickKs.repository.IFeatureRepository
import kotlin.time.Duration.Companion.seconds

class RepeatableEventTriggeringTest(
    private val featureClient: FeatureClient,
    private val eventSource: ManualEventSource,
    private val featureRepo: IFeatureRepository,
) : BaseIntegrationTest({
    val self = this as RepeatableEventTriggeringTest

    "Create simple event trigger" {
        val userCreatedFeature = featureClient.createFeatureAndWaitApply(
            SaveFeatureRequest(
            id = null,
            code = DUMMY_EVENT_TRIGGER
        ))

        val timesToRepeat = 100
        self.sendManyEvents(timesToRepeat)
        delay(5.seconds)

        val updatedFeature = featureRepo.findById(userCreatedFeature.id)!!
        updatedFeature.successExecutionsAmount shouldBe timesToRepeat
    }
}) {

    suspend fun sendManyEvents(timesToRepeat: Int) = coroutineScope {
            repeat(timesToRepeat) {
                launch {
                    sendDummyEvent()
                }
            }
    }

    suspend fun sendDummyEvent() {
        eventSource.manuallyPublishEvent(
            TgNewTextMessageContext(
                id = 0,
                chatId = 0,
                text = "Hi!",
                sender = mockk()
            )
        )
    }

    companion object {
        const val DUMMY_EVENT_TRIGGER = """
            feature {

            name = "Фича"
            onEvent {
                newTelegramMessage {
                    execute {
                       
                    }
                }
            }
        }
        """
    }
}