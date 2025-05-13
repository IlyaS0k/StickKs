package ru.ilyasok.StickKs.service

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import ru.ilyasok.StickKs.model.NotificationMessage
import ru.ilyasok.StickKs.model.NotificationType
import ru.ilyasok.StickKs.ws.WebSocketSessionManager
import java.util.UUID

@Service
class NotificationService(
    private val sessionManager: WebSocketSessionManager,
    private val mapper: ObjectMapper,
) {

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java)
    }

    private fun sendWithSession(block: NotificationService.(WebSocketSession) -> NotificationMessage) {
        val session = sessionManager.session()
        if (session?.isOpen == true) {
            val message = block(session)
            logger.info("Send notification message : $message")
        } else {
            throw RuntimeException("Session is not available")
        }
    }

    fun notify(featureId: UUID, reqId: UUID?, notification: NotificationType) = sendWithSession { session ->
        val message = NotificationMessage(
            reqId = reqId,
            id = featureId,
            type = notification
        )

        session.sendMessage(TextMessage(mapper.writeValueAsString(message)))
        message
    }

}