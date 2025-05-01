package ru.ilyasok.StickKs.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.UUID

@Document("features")
data class Feature(
    @Id
    val id: UUID,

    val name: String,

    val code: String
)