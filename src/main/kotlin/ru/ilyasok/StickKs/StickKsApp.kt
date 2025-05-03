package ru.ilyasok.StickKs

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.EnableAspectJAutoProxy
import ru.ilyasok.StickKs.dsl.DSLDependenciesProvider

@SpringBootApplication
@EnableAspectJAutoProxy
class StickKsApp

fun main(args: Array<String>) {
    DSLDependenciesProvider.findDependencies()
    runApplication<StickKsApp>(*args)
}