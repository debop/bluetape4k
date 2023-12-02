package io.bluetape4k.logback.kafka.keyprovider

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.spi.LoggingEvent
import io.bluetape4k.logging.KLogging
import org.junit.jupiter.api.BeforeEach

abstract class AbstractKeyProviderTest {

    companion object: KLogging()

    protected abstract val keyProvider: KeyProvider<*>
    protected val ctx = LoggerContext()

    protected val sampleEvent = LoggingEvent("fqcn", ctx.getLogger("logger"), Level.ALL, "msg", null, null)

    @BeforeEach
    fun beforeEach() {
        ctx.reset()
    }
}
