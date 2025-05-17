package ru.ilyasok.StickKs.core.utils

import io.github.classgraph.ClassGraph
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Component
import ru.ilyasok.StickKs.dsl.FeatureDSL

@Component
class DSLDependenciesProvider {

    private val dslFeatureAnnotation = FeatureDSL::class.java.name

    private val dependencies = mutableListOf(
        "kotlin.time.Duration.Companion.milliseconds",
        "kotlin.time.Duration.Companion.seconds",
        "kotlin.time.Duration.Companion.minutes",
        "kotlin.time.Duration.Companion.hours",
        "kotlin.time.Duration.Companion.days",
    )

    @PostConstruct
    fun findDependencies() {
        val scanResult = ClassGraph()
            .enableAllInfo()
            .scan()
        val classes = scanResult.allClasses
        val dslExtDependencies = classes.filter { c ->
            c.hasDeclaredMethodAnnotation(dslFeatureAnnotation)
        }.flatMap { c ->
            c.methodInfo.filter { m ->
                m.annotationInfo.firstOrNull { a -> a.name == dslFeatureAnnotation } != null
            }.map { m ->
                c.name.substringBeforeLast(".").plus(".${m.name}")
            }
        }

        dependencies.addAll(dslExtDependencies)
    }

    fun provide(): List<String> = dependencies

    fun provideAsString() = dependencies.joinToString(separator = "\n") { d -> "import $d" }
}