package ru.ilyasok.StickKs.model

import java.util.UUID

data class SaveFeatureRequest(
    val id: UUID?,
    val code: String
)