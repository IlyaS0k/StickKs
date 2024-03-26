package ru.ilyasok.StickKs

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class StickKsApp
    fun main(args: Array<String>) {
        runApplication<StickKsApp>(*args)
    }