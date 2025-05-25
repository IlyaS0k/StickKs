package ru.ilyasok.StickKs.controller

import kotlinx.coroutines.flow.toList
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.ilyasok.StickKs.exception.FeatureOperationException
import ru.ilyasok.StickKs.model.FeatureModel
import ru.ilyasok.StickKs.service.FeatureService
import java.util.UUID

@RestController
@RequestMapping("/info")
class InfoController(
    private val featureService: FeatureService
) {

    @GetMapping("/features")
    suspend fun featuresInfo(): List<FeatureModel> {
        try {
            return featureService.getAll().toList()
        } catch (e: Throwable) {
            throw FeatureOperationException("Failed to get features: " + e.message)
        }
    }

    @GetMapping("/features/{guid}")
    suspend fun featureInfo(
        @PathVariable guid: UUID
    ): FeatureModel {
        try {
            return featureService.getById(guid)
        } catch (e: Throwable) {
            throw FeatureOperationException("Failed to get feature: " + e.message)
        }
    }
}