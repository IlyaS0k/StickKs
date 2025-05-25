package ru.ilyasok.StickKs.ws

import org.springframework.stereotype.Component
import org.springframework.web.socket.WebSocketSession

@Component
class WebSocketSessionManager {
    private var sessions: MutableList<WebSocketSession> = mutableListOf()

    fun sessions(): List<WebSocketSession> = synchronized(this) { sessions.toList() }

    fun addSession(session: WebSocketSession) = synchronized(this) {
        this.sessions.add(session)
    }

    fun closeSession(session: WebSocketSession) = synchronized(this) {
        this.sessions.remove(session)
    }

}