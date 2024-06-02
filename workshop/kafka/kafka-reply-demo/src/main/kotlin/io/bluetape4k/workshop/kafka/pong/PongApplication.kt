package io.bluetape4k.workshop.kafka.pong

import io.bluetape4k.logging.KLogging
import io.bluetape4k.testcontainers.massage.KafkaServer
import org.springframework.boot.WebApplicationType
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class PongApplication {

    companion object: KLogging() {
        private val kafka = KafkaServer.Launcher.kafka

        const val TOPIC_PINGPONG = "pingpong"
    }
}

fun main(vararg args: String) {
    runApplication<PongApplication>(*args) {
        webApplicationType = WebApplicationType.NONE
    }
}
