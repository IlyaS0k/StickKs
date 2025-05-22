package ru.ilyasok.StickKs.core.utils

import io.github.classgraph.ClassGraph
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import ru.ilyasok.StickKs.dsl.FeatureDSL
import java.io.File
import java.util.UUID

@Component
class DSLDependenciesProvider(
    @param:Value("\${dsl-dependencies}")
    private val dslDependenciesPath: String,
) {
    private val dslFeatureAnnotation = FeatureDSL::class.java.name

    private val dependencies: MutableList<String> = if (File(dslDependenciesPath).exists()) {
        File(dslDependenciesPath).readLines().toMutableList()
    } else {
        mutableListOf()
    }

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