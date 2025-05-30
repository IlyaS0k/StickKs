package ru.ilyasok.StickKs.core.utils

import jakarta.annotation.PostConstruct
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component
import java.time.Clock

@Component
class SpringContext(private val context: ApplicationContext) {

    companion object {
        private lateinit var SPRING_CONTEXT: SpringContext

        fun <T> getBean(beanClass: Class<T>): T {
            return SPRING_CONTEXT.context.getBean(beanClass)
        }

        fun clock(): Clock {
            return getBean(Clock::class.java)
        }
    }

    @PostConstruct
    fun initSpringContext() {
        SPRING_CONTEXT = this
    }

}