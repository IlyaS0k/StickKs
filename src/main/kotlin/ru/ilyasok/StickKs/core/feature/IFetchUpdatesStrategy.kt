package ru.ilyasok.StickKs.core.feature

import org.slf4j.LoggerFactory
import kotlin.coroutines.cancellation.CancellationException

interface IFetchUpdatesStrategy {

    fun name(): String

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java)
    }

    suspend fun loop() {
        while (true) {
            try {
                this.fetch()
            }
            catch (_: CancellationException) {
                break
            }
            catch (e: Throwable) {
                logger.warn("Error while fetching updates ", e)
            }
        }
    }

    suspend fun fetch()
}