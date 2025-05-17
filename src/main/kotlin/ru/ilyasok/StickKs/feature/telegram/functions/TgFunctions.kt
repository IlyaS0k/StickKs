package ru.ilyasok.StickKs.feature.telegram.functions

import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import ru.ilyasok.StickKs.tdapi.client.abstraction.ITgClient


@Component
@Profile("!test")
class TgFunctions(val client: ITgClient) {

}