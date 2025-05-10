package ru.ilyasok.StickKs.repository

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import ru.ilyasok.StickKs.model.FeatureModel
import java.util.UUID

@Repository
interface IFeatureRepository : CoroutineCrudRepository<FeatureModel, UUID>