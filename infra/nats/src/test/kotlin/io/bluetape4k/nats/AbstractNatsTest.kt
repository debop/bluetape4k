package io.bluetape4k.nats

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.nats.client.options
import io.bluetape4k.testcontainers.massage.NatsServer
import io.bluetape4k.utils.ShutdownQueue
import io.nats.client.Connection
import io.nats.client.Nats
import io.nats.client.impl.ErrorListenerLoggerImpl

abstract class AbstractNatsTest {

    companion object: KLogging() {
        val nats = NatsServer.Launcher.nats
        val natsDefault by lazy {
            NatsServer(useDefaultPort = true).apply {
                start()
                ShutdownQueue.register(this)
            }
        }
        const val TEST_SUBJECT = "subject.test.1"
    }

    protected fun getConnection(): Connection {
        val options = options {
            server(nats.url)
            connectionListener { conn, event ->
                conn.servers.forEach { server ->
                    log.debug { "server: ${server}, event=${event.name}" }
                }
            }
            errorListener(ErrorListenerLoggerImpl())
        }
        return Nats.connect(options)
    }
}
