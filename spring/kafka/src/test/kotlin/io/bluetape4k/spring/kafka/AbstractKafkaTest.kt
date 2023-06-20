package io.bluetape4k.spring.kafka

import io.bluetape4k.logging.KLogging
import io.bluetape4k.testcontainers.massage.KafkaServer

abstract class AbstractKafkaTest {

    companion object: KLogging() {

        private val kafkaServer = KafkaServer.Launcher.kafka

    }

}
