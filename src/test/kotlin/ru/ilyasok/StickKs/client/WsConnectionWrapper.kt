package ru.ilyasok.StickKs.client

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import jakarta.annotation.PostConstruct
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.reactor.mono
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service
import org.springframework.web.reactive.socket.WebSocketMessage
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient
import reactor.core.publisher.Mono
import ru.ilyasok.StickKs.model.NotificationMessage
import java.io.Closeable
import java.net.URI

@Service
@Lazy
class WsConnectionWrapper(
    @LocalServerPort private val port: Int,
    private val mapper: ObjectMapper
): Closeable {

    private val sendChannel = Channel<String>()
    private val coroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    val receiveChannel = Channel<NotificationMessage>()

    @PostConstruct
    fun afterConstruction() {
        val uri = URI.create("ws://localhost:$port/ws")
        val client = ReactorNettyWebSocketClient()
        val clientPublisher = client.execute(uri) { session ->
            val sendMono = mono {
                coroutineScope.launch {
                    for (message in sendChannel) {
                        session.send(
                            Mono.just(session.textMessage(message))
                        ).awaitFirst()
                    }
                }
            }
            val receiveMono = session.receive()
                .map(WebSocketMessage::getPayloadAsText)
                .map { message -> coroutineScope.launch {
                    val event = mapper.readValue<NotificationMessage>(message)
                    receiveChannel.send(event)
                } }
                .then()

            sendMono.and(receiveMono)
        }
        coroutineScope.launch {
            clientPublisher.awaitSingleOrNull()
        }
    }

    fun getAllReceivedEvents() = buildList {
        while (true) {
            val receive = receiveChannel.tryReceive()
            if (!receive.isSuccess) {
                break
            } else {
                add(receive.getOrThrow())
            }

        }
    }

    suspend fun send(text: String) {
        sendChannel.send(text)
    }

    override fun close() {
        coroutineScope.cancel()
    }

}