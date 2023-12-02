package io.bluetape4k.logback.kafka.keyprovider

import io.bluetape4k.logback.kafka.utils.hashBytes
import org.amshove.kluent.shouldContainSame
import org.junit.jupiter.api.Test

class LoggerNameKeyProviderTest: AbstractKeyProviderTest() {

    override val keyProvider = LoggerNameKeyProvider()

    @Test
    fun `get key value by logger name`() {
        keyProvider.get(sampleEvent)!! shouldContainSame sampleEvent.loggerName.hashBytes()!!
    }
}
