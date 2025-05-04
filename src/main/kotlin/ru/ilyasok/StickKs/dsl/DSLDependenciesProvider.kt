package ru.ilyasok.StickKs.dsl

import io.github.classgraph.ClassGraph

class DSLDependenciesProvider {

    companion object {

        private val DSL_FEATURE_ANNOTATION = FeatureDSL::class.java.name

        private val dependencies = mutableListOf<String>(
            "kotlin.time.Duration.Companion.milliseconds",
            "kotlin.time.Duration.Companion.seconds",
            "kotlin.time.Duration.Companion.minutes",
            "kotlin.time.Duration.Companion.hours",
            "kotlin.time.Duration.Companion.days",
        )

        fun findDependencies() {
            val scanResult = ClassGraph()
                .enableAllInfo()
                .scan()
            val classes = scanResult.allClasses
            val dslExtDependencies = classes.filter { c ->
                c.hasDeclaredMethodAnnotation(DSL_FEATURE_ANNOTATION)
            }.flatMap { c ->
                c.methodInfo.filter { m ->
                    m.annotationInfo.firstOrNull { a -> a.name == DSL_FEATURE_ANNOTATION } != null
                }.map { m ->
                    c.name.substringBeforeLast(".").plus(".${m.name}")
                }
            }

            dependencies.addAll(dslExtDependencies)
        }

        fun provide(): List<String> = dependencies

        fun provideAsString() = dependencies.joinToString(separator = "\n") { d -> "import $d" }
    }
}