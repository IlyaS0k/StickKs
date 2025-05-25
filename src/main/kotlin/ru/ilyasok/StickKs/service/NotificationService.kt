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

    private fun sendToAll(block: NotificationService.(WebSocketSession) -> NotificationMessage) {
        val sessions = sessionManager.sessions()
        for (session in sessions) {
            if (session.isOpen) {
                val message = block(session)
                logger.info("Send notification message : $message")
            } else {
                logger.info("Session is not available")
            }
        }
    }

    fun notify(featureId: UUID, reqId: UUID?, notification: NotificationType) = sendToAll { session ->
        val message = NotificationMessage(
            reqId = reqId,
            id = featureId,
            type = notification
        )

        session.sendMessage(TextMessage(mapper.writeValueAsString(message)))
        message
    }

}