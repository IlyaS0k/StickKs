package ru.ilyasok.StickKs.ws

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.socket.WebSocketSession
import java.time.Instant
import kotlin.time.Duration
import kotlin.time.toJavaDuration

@Component
class WebSocketSessionManager {
    private var session: WebSocketSession? = null
    private var meta = SessionMeta()

    companion object {
        private val logger = LoggerFactory.getLogger(WebSocketSessionManager::class.java)
    }

    private data class SessionMeta(
        val lastActiveTime: Instant? = null,
    )

    fun session(): WebSocketSession? = session

    fun openSession(session: WebSocketSession) = synchronized(this) {
        this.session = session
    }

    fun closeSession(session: WebSocketSession) = synchronized(this) {
        meta = meta.copy(lastActiveTime = Instant.now())
    }

    fun isInactiveFor(duration: Duration): Boolean = synchronized(this) {
        return try {
            if (session == null) return true
            if (meta.lastActiveTime == null) return false
            !session!!.isOpen && meta.lastActiveTime!!.plus(duration.toJavaDuration()).isBefore(Instant.now())
        } catch (e : Throwable) {
            logger.error("Error during session inactivity check ", e)
            true
        }
    }

}