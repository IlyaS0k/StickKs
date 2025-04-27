package ru.ilyasok.StickKs

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.EnableAspectJAutoProxy

@SpringBootApplication
@EnableAspectJAutoProxy
class StickKsApp

fun main(args: Array<String>) {
    runApplication<StickKsApp>(*args)
}