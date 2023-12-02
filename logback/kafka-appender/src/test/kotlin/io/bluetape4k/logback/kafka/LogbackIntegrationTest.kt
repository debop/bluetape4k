package io.bluetape4k.logback.kafka

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.info
import io.bluetape4k.support.toUtf8String
import io.bluetape4k.testcontainers.massage.KafkaServer
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldContain
import org.amshove.kluent.shouldNotBeNull
import org.apache.kafka.common.TopicPartition
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import java.time.Duration

class LogbackIntegrationTest {

    companion object: KLogging()

    private lateinit var kafka: KafkaServer
    private lateinit var logger: org.slf4j.Logger

    @BeforeAll
    fun beforeAll() {
        kafka = KafkaServer(useDefaultPort = true).apply { start() }
        logger = LoggerFactory.getLogger("LogbackIntegrationTest")

        log.info { "Create Kafka Server: ${kafka.bootstrapServers}" }
    }

    @AfterAll
    fun afterAll() {
        if (::kafka.isInitialized) {
            log.info { "Shutdown Kafka Server ..." }
            kafka.close()
        }
    }

    @Test
    fun `export log to kafka and consume`() {
        val logSize = 100
        repeat(logSize) {
            logger.info("test message $it")
        }

        Thread.sleep(100)

        val logTopicPartition = TopicPartition("logs", 0)
        val consumer = KafkaServer.Launcher.createBinaryConsumer(kafka)
        consumer.assign(listOf(logTopicPartition))
        consumer.seekToBeginning(listOf(logTopicPartition))

        var receivedCount = 0
        var records = consumer.poll(Duration.ofSeconds(1))
        while (!records.isEmpty) {
            records.forEach { record ->
                val message = record.value()?.toUtf8String()
                println("received: $message")
                message.shouldNotBeNull() shouldContain "test message $receivedCount"
                receivedCount++
            }
            records = consumer.poll(Duration.ofSeconds(1))
        }

        receivedCount shouldBeEqualTo logSize
    }
}
