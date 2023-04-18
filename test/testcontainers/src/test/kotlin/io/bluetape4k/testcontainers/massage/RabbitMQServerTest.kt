package io.bluetape4k.testcontainers.massage

import com.rabbitmq.client.AMQP
import com.rabbitmq.client.ConnectionFactory
import com.rabbitmq.client.DefaultConsumer
import com.rabbitmq.client.Envelope
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import java.util.Arrays
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeTrue
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode

@Execution(ExecutionMode.SAME_THREAD)
class RabbitMQServerTest {

    companion object: KLogging() {
        const val RABBITMQ_TEST_EXCHANGE = "TestExchange"
        const val RABBITMQ_TEST_ROUTING_KEY = "TestRoutingKey"
        const val RABBITMQ_TEST_MESSAGE = "Hello world"
    }

    private val rabbitMQ = RabbitMQServer.Launcher.rabbitMQ

    @Test
    fun `run rabbitmq server`() {
        RabbitMQServer.Launcher.rabbitMQ.isRunning.shouldBeTrue()
        log.debug { "host=${rabbitMQ.host}" }
        log.debug { "port=${rabbitMQ.port}" }
    }

    @Test
    fun `run rabbitmq server with default port`() {
        RabbitMQServer(useDefaultPort = true)
            .apply { start() }
            .use { rabbitmq ->
                rabbitmq.isRunning.shouldBeTrue()
                log.debug { "url = ${rabbitmq.url}" }

                rabbitmq.port shouldBeEqualTo RabbitMQServer.AMQP_PORT
            }
    }

    @Test
    fun `connect to rabbitmq server`() {
        val factory = ConnectionFactory().apply {
            host = rabbitMQ.host
            port = rabbitMQ.port
        }

        val connection = factory.newConnection()
        connection.shouldNotBeNull()

        val channel = connection.createChannel()
        channel.shouldNotBeNull()
        channel.exchangeDeclare(RABBITMQ_TEST_EXCHANGE, "direct", true)

        val queueName = channel.queueDeclare().queue
        channel.queueBind(queueName, RABBITMQ_TEST_EXCHANGE, RABBITMQ_TEST_ROUTING_KEY)

        // Set up a consumer on the queue
        var messageWasReceived = false
        channel.basicConsume(queueName, false, object: DefaultConsumer(channel) {
            override fun handleDelivery(
                consumerTag: String?,
                envelope: Envelope?,
                properties: AMQP.BasicProperties?,
                body: ByteArray?,
            ) {
                messageWasReceived = Arrays.equals(body, RABBITMQ_TEST_MESSAGE.toByteArray())
            }
        })

        // post a message
        channel.basicPublish(
            RABBITMQ_TEST_EXCHANGE,
            RABBITMQ_TEST_ROUTING_KEY,
            null,
            RABBITMQ_TEST_MESSAGE.toByteArray()
        )

        // check the message was received
        Thread.sleep(1000)
        messageWasReceived.shouldBeTrue()

        connection.close()
    }
}
