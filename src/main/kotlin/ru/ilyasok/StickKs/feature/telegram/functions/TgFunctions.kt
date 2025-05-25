package ru.ilyasok.StickKs.feature.telegram.functions

import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import ru.ilyasok.StickKs.dsl.execute
import ru.ilyasok.StickKs.dsl.feature
import ru.ilyasok.StickKs.dsl.onEvent
import ru.ilyasok.StickKs.dsl.withCondition
import ru.ilyasok.StickKs.dsl.withTimeout
import ru.ilyasok.StickKs.feature.telegram.entities.user
import ru.ilyasok.StickKs.feature.telegram.newTelegramMessage
import ru.ilyasok.StickKs.tdapi.client.abstraction.ITgClient
import kotlin.time.Duration.Companion.minutes


@Component
@Profile("!test")
class TgFunctions(val client: ITgClient) {
    fun test() {
        feature {
            name = "delete msg"

            withTimeout {
                afterStart = 0.minutes
                timeout = 60.minutes
            }

            onEvent {
                newTelegramMessage {
                    withCondition { message ->
                        listOf("ilyaS0k", "natalia_sharshakova").contains(message.sender.usernames.editableUsername)
                    }

                    execute { message ->
                        sendMessageTo(user { username = "k1ssik" }) { "Привет" }
                    }
                }
            }
        }

        feature {
            onEvent {
                newTelegramMessage {
                    execute {
                        println("XIAOAAAAAAAAAAAAAAAAAAAAA")
                    }
                }
            }
        }
    }

}