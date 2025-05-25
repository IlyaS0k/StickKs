package ru.ilyasok.StickKs.ws

import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import ru.ilyasok.StickKs.core.feature.FeatureProcessingManager

@Component
class WebSocketHandler(
    private val sessionManager: WebSocketSessionManager,
    private val manager: FeatureProcessingManager
) : TextWebSocketHandler() {

    override fun afterConnectionEstablished(session: WebSocketSession) {
        if (session.isOpen) {
            sessionManager.addSession(session)
            runBlocking {
                manager.enable(true)
            }
        }
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        sessionManager.closeSession(session)
    }
}