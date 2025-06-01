package ru.ilyasok.StickKs.feature.telegram.functions

import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import ru.ilyasok.StickKs.core.context.contextMap
import ru.ilyasok.StickKs.dsl.feature
import ru.ilyasok.StickKs.dsl.onevent.execute
import ru.ilyasok.StickKs.dsl.onevent.onEvent
import ru.ilyasok.StickKs.dsl.periodic
import ru.ilyasok.StickKs.dsl.trigger.execute
import ru.ilyasok.StickKs.dsl.trigger.trigger
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

            context = contextMap {
                "xiao" to "cocka"
                "one" to 1
            }

            periodic {
                afterStart = 3.seconds
                period = 5.seconds
            }

            onEvent {
                newTelegramMessage {
                    execute {
                        val context = this
                        context.put("xiao2" to "cocka2")
                        println(context.get<String>("xiao2"))
                    }
                }
            }
        }

        feature {
            name = "Xiao Feature run"

            context = contextMap {
                "xiao" to "cocka"
                "one" to 1
            }

            periodic {
                afterStart = 3.seconds
                period = 5.seconds
            }

            trigger {
                execute {
                    val context = this
                    context.put("xiao2" to "cocka2")
                    val v = context.get<String>("xiao")
                    sendMessageTo(user { username = "k1ss1k" }) { v }
                }
            }
        }
    }
}