package ru.ilyasok.StickKs.config

import jakarta.annotation.PostConstruct
import kotlinx.coroutines.debug.DebugProbes
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@Profile("debug")
class DebugProbesInstaller {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(this::class.java)
    }

    @PostConstruct
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    fun installDebugProbes() {
        DebugProbes.install()
        logger.debug("****DEBUG PROBES IS INSTALLED****")
    }
}