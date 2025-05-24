package ru.ilyasok.StickKs.config

import com.github.dockerjava.api.model.ExposedPort
import com.github.dockerjava.api.model.HostConfig
import com.github.dockerjava.api.model.PortBinding
import com.github.dockerjava.api.model.Ports
import org.flywaydb.core.Flyway
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Bean
import org.testcontainers.containers.PostgreSQLContainer

@TestConfiguration
class PostgresContainerConfig(
    @Value("\${postgres-docker.image}")
    private val postgresDockerImage: String,
    @Value("\${postgres-docker.db-name}")
    private val postgresDockerDbname: String,
    @Value("\${postgres-docker.user}")
    private val postgresDockerUser: String,
    @Value("\${postgres-docker.password}")
    private val postgresDockerPassword: String,
    @Value("\${postgres-docker.port.container}")
    private val postgresDockerContainerPort: Int,
    @Value("\${postgres-docker.port.host}")
    private val postgresDockerHostPort: Int
) {

    @Bean(initMethod = "migrate")
    fun flyway(): Flyway {
        val datasourceUrl = "jdbc:postgresql://localhost:$postgresDockerHostPort/$postgresDockerDbname"

        return Flyway.configure()
            .dataSource(datasourceUrl, postgresDockerUser, postgresDockerPassword)
            .baselineOnMigrate(true)
            .load()
    }


    @Bean
    @ServiceConnection
    fun postgresContainer(): PostgreSQLContainer<*> {
        return PostgreSQLContainer<Nothing>(postgresDockerImage).apply {
            withCreateContainerCmdModifier { cmd ->
                cmd.withHostConfig(
                    HostConfig().withPortBindings(
                        PortBinding(
                            Ports.Binding.bindPort(postgresDockerHostPort),
                            ExposedPort(postgresDockerContainerPort)
                        )
                    )
                )
            }
            withExposedPorts(postgresDockerContainerPort)
            withDatabaseName(postgresDockerDbname)
            withUsername(postgresDockerUser)
            withPassword(postgresDockerPassword)
        }
    }
}