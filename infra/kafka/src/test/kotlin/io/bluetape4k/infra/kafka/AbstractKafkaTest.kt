package io.bluetape4k.infra.kafka

import io.bluetape4k.core.LibraryName
import io.bluetape4k.junit5.faker.Fakers
import io.bluetape4k.logging.KLogging
import io.bluetape4k.testcontainers.massage.KafkaServer

abstract class AbstractKafkaTest {

    companion object: KLogging() {
        const val TEST_TOPIC_NAME = "$LibraryName.kafka.test-topic.1"
        const val REPEAT_SIZE = 5

        @JvmStatic
        fun randomString(): String =
            Fakers.randomString(128, 1024, true)

        val kafka: KafkaServer by lazy { KafkaServer.Launcher.kafka }
    }
}
