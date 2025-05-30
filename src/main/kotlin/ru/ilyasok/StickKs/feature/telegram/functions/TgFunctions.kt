package ru.ilyasok.StickKs.feature.telegram.functions

import kotlinx.coroutines.NonCancellable.cancel
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import ru.ilyasok.StickKs.dsl.execute
import ru.ilyasok.StickKs.dsl.feature
import ru.ilyasok.StickKs.dsl.onEvent
import ru.ilyasok.StickKs.dsl.periodically
import ru.ilyasok.StickKs.dsl.schedule
import ru.ilyasok.StickKs.dsl.trigger
import ru.ilyasok.StickKs.feature.telegram.entities.user
import ru.ilyasok.StickKs.feature.telegram.newTelegramMessage
import ru.ilyasok.StickKs.tdapi.client.abstraction.ITgClient
import kotlin.time.Duration.Companion.seconds


@Component
@Profile("!test")
class TgFunctions(val client: ITgClient) {
    fun test() {
        feature {
            name = "Xiao Feature run"

            periodically {
                afterStart = 3.seconds
                period = 5.seconds
            }

            trigger {
                execute {
                    sendMessageTo(user { username = "k1ss1k" }) { "Привет" }
                }
            }
        }

        feature {
            name = "Xiao Feature Scheduled"

            schedule {
                cron = "* * * * *"
            }

            trigger {
                execute {
                    sendMessageTo(user { username = "k1ss1k" }) { "Привет" }
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