package ru.ilyasok.StickKs.repository

import org.springframework.data.mongodb.repository.MongoRepository
import ru.ilyasok.StickKs.model.Feature
import java.util.UUID

interface IFeatureRepository : MongoRepository<Feature, UUID>