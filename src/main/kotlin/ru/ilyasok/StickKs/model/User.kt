package ru.ilyasok.StickKs.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.util.UUID

@Document("users")
class User(
    @Id
    var id: UUID,

    @Indexed(unique = true)
    var phone: String,

    
)