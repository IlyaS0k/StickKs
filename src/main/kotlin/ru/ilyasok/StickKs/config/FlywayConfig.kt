package ru.ilyasok.StickKs.config

import org.flywaydb.core.Flyway
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@Profile("!test")
class FlywayConfig {

    @Qualifier("flyway")
    @Bean(initMethod = "migrate")
    fun flyway(
        @Value("\${spring.flyway.url}") url: String,
        @Value("\${spring.flyway.user}") user: String,
        @Value("\${spring.flyway.password}") password: String
    ): Flyway {
        return Flyway.configure()
            .dataSource(url, user, password)
            .baselineOnMigrate(true)
            .load()
    }
}