package ru.ilyasok.StickKs.repository.abstraction

import org.springframework.data.mongodb.repository.MongoRepository
import ru.ilyasok.StickKs.model.User
import java.util.UUID

interface IUserRepository : MongoRepository<User, UUID> {
    fun findByPhone(phone: String): User?
}