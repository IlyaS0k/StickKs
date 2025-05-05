package ru.ilyasok.StickKs.repository

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import ru.ilyasok.StickKs.model.Feature
import java.util.UUID

interface IFeatureRepository : CoroutineCrudRepository<Feature, UUID> {
    suspend fun getById(id: UUID): Feature?
}