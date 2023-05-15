package io.bluetape4k.workshop.webflux.config

import io.bluetape4k.workshop.webflux.handler.ReactiveWebSocketHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.HandlerMapping
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter

@Configuration
class ReactiveWebSocketConfiguration {

    @Bean
    fun webSocketHandlerAdapter(): WebSocketHandlerAdapter {
        return WebSocketHandlerAdapter()
    }

    @Bean
    fun webSocketHandlerMapping(reactiveWebSocketHandler: ReactiveWebSocketHandler): HandlerMapping {
        val map = mapOf<String, Any>(
            "/event-emitter" to reactiveWebSocketHandler
        )
        return SimpleUrlHandlerMapping().apply {
            order = 1
            urlMap = map
        }
    }
}
