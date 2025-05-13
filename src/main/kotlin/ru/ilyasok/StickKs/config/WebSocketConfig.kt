package ru.ilyasok.StickKs.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry
import ru.ilyasok.StickKs.ws.WebSocketHandler

@Configuration
@EnableWebSocket
class WebSocketConfig(
    private val webSocketHandler: WebSocketHandler
) : WebSocketConfigurer {
    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        registry
            .addHandler(webSocketHandler, "/ws")
            .setAllowedOrigins("*")
    }
}
