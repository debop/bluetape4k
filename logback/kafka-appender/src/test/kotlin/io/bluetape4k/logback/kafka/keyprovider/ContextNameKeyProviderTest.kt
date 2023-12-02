package io.bluetape4k.logback.kafka.keyprovider

import io.bluetape4k.logback.kafka.utils.hashBytes
import io.bluetape4k.logging.KLogging
import org.amshove.kluent.shouldContainSame
import org.junit.jupiter.api.Test

class ContextNameKeyProviderTest: AbstractKeyProviderTest() {

    companion object: KLogging() {
        private const val LOGGER_CONTEXT_NAME = "logger-context-name"
    }

    override val keyProvider = ContextNameKeyProvider()


    @Test
    fun `get key value by context name`() {
        ctx.name = LOGGER_CONTEXT_NAME
        keyProvider.context = ctx

        keyProvider.get(sampleEvent)!! shouldContainSame LOGGER_CONTEXT_NAME.hashBytes()!!
    }
}
