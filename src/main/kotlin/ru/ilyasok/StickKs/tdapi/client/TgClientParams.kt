package ru.ilyasok.StickKs.tdapi.client

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile("!test")
class TgClientParams {
    @Value("\${tdlib.config.database-directory}")
    lateinit var databaseDirectory: String
    @Value("#{new Boolean('\${tdlib.config.use-message-database}')}")
    var useMessageDatabase: Boolean? = null
    @Value("#{new Boolean('\${tdlib.config.use-secret-chats}')}")
    var useSecretChats: Boolean? = null
    @Value("#{new Integer('\${tdlib.config.api-id}')}")
    var apiId: Int? = null
    @Value("\${tdlib.config.api-hash}")
    lateinit var apiHash: String
    @Value("\${tdlib.config.system-language-code}")
    lateinit var systemLanguageCode: String
    @Value("\${tdlib.config.device-model}")
    lateinit var deviceModel: String
    @Value("\${tdlib.config.application-version}")
    lateinit var applicationVersion: String

}
