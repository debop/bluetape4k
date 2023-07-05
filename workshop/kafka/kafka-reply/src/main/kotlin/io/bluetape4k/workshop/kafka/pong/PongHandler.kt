package io.bluetape4k.workshop.kafka.pong

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.info
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class PongHandler {

    companion object: KLogging()

    @KafkaListener(groupId = "pong", topics = [PongApplication.TOPIC_PINGPONG])
    @SendTo // use default replyTo expression
    fun handle(request: String): String {
        log.info { "Received: $request in ${this.javaClass.name}" }
        return "pong at " + LocalDateTime.now()
    }
}
