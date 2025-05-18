package ru.ilyasok.StickKs.controller

import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import ru.ilyasok.StickKs.exception.FeatureOperationException
import ru.ilyasok.StickKs.model.DeleteFeatureRequest
import ru.ilyasok.StickKs.model.FeatureErrorsModel
import ru.ilyasok.StickKs.model.FeatureModel
import ru.ilyasok.StickKs.model.SaveFeatureRequest
import ru.ilyasok.StickKs.service.FeatureErrorsService
import ru.ilyasok.StickKs.service.FeatureService
import java.util.UUID


@Controller
@RequestMapping("/features")
class FeatureController(
    private val featureService: FeatureService,
    private val featureErrorsService: FeatureErrorsService,
) {

    @GetMapping
    suspend fun featureEditor(model: Model): String {
        val features = featureService.getAll().toList()
        model.addAttribute("features", features)
        return "feature-editor"
    }

    @PostMapping("/save")
    @ResponseBody
    suspend fun save(
        @RequestHeader("X-Request-ID") reqId: UUID,
        @RequestBody req: SaveFeatureRequest
    ): FeatureModel {
        try {
            val feature = featureService.save(req.id, reqId, req.code)
            return feature
        } catch (e: Exception) {
            throw FeatureOperationException(e.message ?: "")
        }
    }

    @PostMapping("/delete")
    @ResponseBody
    suspend fun delete(
        @RequestHeader("X-Request-ID") reqId: UUID,
        @RequestParam("id") featureId: UUID
    ) {
        try {
            return featureService.delete(featureId, reqId)
        } catch (e: Exception) {
            throw FeatureOperationException("Failed to delete feature: " + e.message)
        }
    }

    @GetMapping("/errors")
    suspend fun errors(
        @RequestParam("id") featureId: UUID,
        @RequestParam limit: Int = 25,
        model: Model
    ): String {
        try {
            val errors = featureErrorsService.getFeatureErrors(featureId, limit).toList()
            model.addAttribute("errors", errors)
            return "error-logs"
        } catch (e: Exception) {
            throw FeatureOperationException("Failed to get feature errors: " + e.message)
        }
    }

}