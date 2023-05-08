package io.bluetape4k.workshop.stomp.websocket

import io.bluetape4k.logging.KLogging
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class StompWebSocketApplication {

    companion object: KLogging()
}

fun main(vararg args: String) {
    runApplication<StompWebSocketApplication>(*args)
}
