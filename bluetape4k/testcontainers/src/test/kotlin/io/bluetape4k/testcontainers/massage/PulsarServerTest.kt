package io.bluetape4k.testcontainers.massage

import io.bluetape4k.logging.KLogging
import io.bluetape4k.support.toUtf8String
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeTrue
import org.apache.pulsar.client.api.PulsarClient
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import java.util.concurrent.TimeUnit

@Execution(ExecutionMode.SAME_THREAD)
class PulsarServerTest {

    companion object: KLogging() {
        private const val TOPIC_NAME = "pulsar.test-topic.1"
    }

    @Test
    fun `create Pulsar server`() {
        PulsarServer().use { pulsar ->
            pulsar.start()
            pulsar.isRunning.shouldBeTrue()

            verifyPulsarMessaging(pulsar)
        }
    }

    @Test
    fun `create Pulsar server with default port`() {
        PulsarServer(useDefaultPort = true).use { pulsar ->
            pulsar.start()
            pulsar.isRunning.shouldBeTrue()

            pulsar.port shouldBeEqualTo PulsarServer.PORT
            pulsar.brokerPort shouldBeEqualTo PulsarServer.PORT
            pulsar.brokerHttpPort shouldBeEqualTo PulsarServer.HTTP_PORT

            verifyPulsarMessaging(pulsar)
        }
    }

    private fun verifyPulsarMessaging(pulsar: PulsarServer) {
        val brokerUrl = pulsar.pulsarBrokerUrl
        val client = PulsarClient.builder()
            .serviceUrl(brokerUrl)
            .build()

        try {

            val consumer = client.newConsumer()
                .topic(TOPIC_NAME)
                .subscriptionName("test-stubs")
                .subscribe()

            val producer = client.newProducer().topic(TOPIC_NAME).create()

            val messageBody = "test message"
            producer.send(messageBody.toByteArray(Charsets.UTF_8))

            val future = consumer.receiveAsync()
            val message = future.get(5, TimeUnit.SECONDS)

            message.data.toUtf8String() shouldBeEqualTo messageBody

            producer.close()
            consumer.close()
        } finally {
            client.close()
        }
    }
}
