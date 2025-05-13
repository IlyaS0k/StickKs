package ru.ilyasok.StickKs.ws

import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import ru.ilyasok.StickKs.core.feature.FeatureManager

@Component
class WebSocketHandler(
    private val sessionManager: WebSocketSessionManager,
    private val featureManager: FeatureManager
) : TextWebSocketHandler() {

    override fun afterConnectionEstablished(session: WebSocketSession) {
        if (session.isOpen) {
            sessionManager.openSession(session)
            featureManager.enable()
        }
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        sessionManager.closeSession(session)
        //featureManager.disable()
    }

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        val inputText = message.payload
        val response = "Вы сказали: $inputText"
        session.sendMessage(TextMessage(response))
    }
}