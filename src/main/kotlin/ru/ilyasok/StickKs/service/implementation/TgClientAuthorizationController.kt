package ru.ilyasok.StickKs.service.implementation

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RestController
import ru.ilyasok.StickKs.service.abstraction.ITgClientAuthorizationController
import ru.ilyasok.StickKs.tdapi.client.abstraction.ITgClient

@RestController
class TgClientAuthorizationController @Autowired constructor(val tgClient: ITgClient) : ITgClientAuthorizationController {




}