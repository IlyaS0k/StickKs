package ru.ilyasok.StickKs

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import ru.ilyasok.StickKs.dsl.DSLDependenciesProvider

@SpringBootApplication
class StickKsApp

fun main(args: Array<String>) {
    DSLDependenciesProvider.findDependencies()
    runApplication<StickKsApp>(*args)
}