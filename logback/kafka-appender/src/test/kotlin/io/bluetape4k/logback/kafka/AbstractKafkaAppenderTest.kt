package io.bluetape4k.logback.kafka

import io.bluetape4k.logging.KLogging
import io.bluetape4k.testcontainers.massage.KafkaServer

abstract class AbstractKafkaAppenderTest {

    companion object: KLogging() {
        private val kakfaServer = KafkaServer.Launcher.kafka
    }
}
