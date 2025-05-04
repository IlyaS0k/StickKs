package ru.ilyasok.StickKs.controller

import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.server.ResponseStatusException
import ru.ilyasok.StickKs.controller.exception.InvalidFeatureException
import ru.ilyasok.StickKs.model.Feature
import ru.ilyasok.StickKs.model.SaveFeatureRequest
import ru.ilyasok.StickKs.service.FeatureService


@Controller
@RequestMapping("/features")
class FeatureController(private val featureService: FeatureService) {

    @GetMapping
    fun featureEditor(model: Model): String {
        val features = featureService.getAllStable()
        model.addAttribute("features", features)
        return "feature-editor"
    }

    @PostMapping("/save")
    @ResponseBody
    fun save(@RequestBody req: SaveFeatureRequest): Feature {
        try {
            return featureService.save(req.id, req.code)
        } catch (e: Exception) {
            throw InvalidFeatureException(e.message ?: "")
        }
    }

}