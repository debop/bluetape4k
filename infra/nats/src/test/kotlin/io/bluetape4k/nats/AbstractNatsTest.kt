package io.bluetape4k.nats

import io.bluetape4k.logging.KLogging
import io.bluetape4k.nats.client.options
import io.bluetape4k.testcontainers.massage.NatsServer
import io.nats.client.Connection
import io.nats.client.Nats

abstract class AbstractNatsTest {

    companion object: KLogging() {
        val nats = NatsServer.Launcher.nats

        const val TEST_SUBJECT = "subject.test.1"
    }

    protected fun getConnection(): Connection {
        val options = options {
            server(nats.url)
            connectionListener { conn, type ->
                println(type)
            }
        }
        return Nats.connect(options)
    }
}
