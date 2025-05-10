package ru.ilyasok.StickKs.controller

import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import ru.ilyasok.StickKs.exception.FeatureOperationException
import ru.ilyasok.StickKs.model.DeleteFeatureRequest
import ru.ilyasok.StickKs.model.FeatureModel
import ru.ilyasok.StickKs.model.FeatureStability
import ru.ilyasok.StickKs.model.SaveFeatureRequest
import ru.ilyasok.StickKs.service.FeatureService
import java.util.UUID


@Controller
@RequestMapping("/features")
class FeatureController(private val featureService: FeatureService) {

    @GetMapping
    suspend fun featureEditor(model: Model): String {
        val features = featureService.getAll().toList()
        model.addAttribute("features", features)
        return "feature-editor"
    }

    @PostMapping("/save")
    @ResponseBody
    suspend fun save(@RequestBody req: SaveFeatureRequest): FeatureModel {
        try {
            val feature = featureService.save(req.id, req.code)
            feature.stability = FeatureStability.STABLE
            return feature
        } catch (e: Exception) {
            throw FeatureOperationException(e.message ?: "")
        }
    }

    @PostMapping("/delete")
    @ResponseBody
    suspend fun delete(@RequestBody req: DeleteFeatureRequest) {
        try {
            return featureService.delete(req.id)
        } catch (e: Exception) {
            throw FeatureOperationException(("Failed to delete feature: " + e.message))
        }
    }

}