package ru.ilyasok.StickKs.feature.telegram.functions

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import ru.ilyasok.StickKs.core.context.contextMap
import ru.ilyasok.StickKs.dsl.always
import ru.ilyasok.StickKs.dsl.feature
import ru.ilyasok.StickKs.dsl.onevent.execute
import ru.ilyasok.StickKs.dsl.onevent.onEvent
import ru.ilyasok.StickKs.dsl.onevent.withCondition
import ru.ilyasok.StickKs.dsl.periodic
import ru.ilyasok.StickKs.dsl.trigger.execute
import ru.ilyasok.StickKs.dsl.trigger.trigger
import ru.ilyasok.StickKs.feature.deepseek.client.DeepseekClient
import ru.ilyasok.StickKs.feature.deepseek.functions.askDeepseek
import ru.ilyasok.StickKs.feature.telegram.entities.user
import ru.ilyasok.StickKs.feature.telegram.newTelegramMessage
import ru.ilyasok.StickKs.tdapi.client.abstraction.ITgClient
import kotlin.time.Duration.Companion.seconds


@Component
@Profile("!test")
@ConditionalOnBean(DeepseekClient::class)
class TgFunctions(
    val client: ITgClient,
    val deepseekClient: DeepseekClient
) {
    fun examples() {
        feature {
            name = "Deepseek answer"

            context = contextMap {
                "user" to "k1ss1k"
            }

            always()

            onEvent {
                newTelegramMessage {
                    withCondition { message ->
                        message.sender.name == this.get<String>("user")
                    }

                    execute { message ->
                        val replyTo = this.get<String>("user")
                        val answer = askDeepseek {
                            """
                                Мне пишет пользователь ${message.sender.name}.
                                Представься моим автоответчиком и сообщи что я занят.
                            """
                        }
                        sendMessageTo(user { username = replyTo } ) { answer }
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