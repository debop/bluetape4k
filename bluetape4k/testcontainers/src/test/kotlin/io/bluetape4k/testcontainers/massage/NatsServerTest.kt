package io.bluetape4k.testcontainers.massage

import io.bluetape4k.logging.KLogging
import io.bluetape4k.support.toUtf8Bytes
import io.bluetape4k.support.toUtf8String
import io.nats.client.Connection
import io.nats.client.Nats
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeNull
import org.amshove.kluent.shouldBeTrue
import org.awaitility.kotlin.await
import org.awaitility.kotlin.until
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import java.time.Duration

@Execution(ExecutionMode.SAME_THREAD)
class NatsServerTest {

    companion object: KLogging()

    @Test
    fun `create nats server`() {
        NatsServer().use { nats ->
            nats.start()
            nats.isRunning.shouldBeTrue()

            connectToNats(nats)
        }
    }

    @Test
    fun `create nats server with default port`() {
        NatsServer(useDefaultPort = true).use { nats ->
            nats.start()
            nats.isRunning.shouldBeTrue()

            connectToNats(nats)
        }
    }

    private fun connectToNats(nats: NatsServer) {
        val client = Nats.connect(nats.url)
        await until { client.status == Connection.Status.CONNECTED }

        val subscription = client.subscribe("subject")
        subscription.isActive.shouldBeTrue()

        client.publish("subject", "Hello world".toUtf8Bytes())
        val message = subscription.nextMessage(Duration.ofMillis(500))

        message.subject shouldBeEqualTo "subject"
        message.subscription shouldBeEqualTo subscription
        message.replyTo.shouldBeNull()
        message.data.toUtf8String() shouldBeEqualTo "Hello world"
    }
}
