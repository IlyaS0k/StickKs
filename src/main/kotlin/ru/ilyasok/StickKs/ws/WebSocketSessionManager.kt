package ru.ilyasok.StickKs.ws

import org.springframework.stereotype.Component
import org.springframework.web.socket.WebSocketSession

@Component
class WebSocketSessionManager {
    private var session: WebSocketSession? = null

    fun session(): WebSocketSession? = session

    fun openSession(session: WebSocketSession) = synchronized(this) {
        this.session = session
    }

}