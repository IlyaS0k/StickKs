package ru.ilyasok.StickKs.controller.implementation

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.ilyasok.StickKs.model.User
import ru.ilyasok.StickKs.repository.abstraction.IUserRepository
import ru.ilyasok.StickKs.tdapi.TdApi
import ru.ilyasok.StickKs.tdapi.client.abstraction.ITgClient
import java.util.UUID

@RestController
@RequestMapping("/test")
class TestController(
    private val client: ITgClient,
    private val userRepository: IUserRepository
) {

    @GetMapping()
    suspend fun test(): String? {
        val users = mutableListOf<TdApi.User>()
        val res = client.getContacts().handle<TdApi.Users?> {
            onSuccess = { res -> res }
            onError = { err -> null }
        }?.userIds?.forEach { userId ->
            client.getUser(userId).handle {
                onSuccess = { res -> res?.let { users.add(res) } }
                onError = { err -> Unit }
            }
        }

        return users[0].toString()
    }

    @GetMapping("/mongo")
    suspend fun testMongo(): User? {
        return userRepository.save(User(UUID.randomUUID(), "Michael Jackson"))
    }

}