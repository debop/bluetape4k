package io.bluetape4k.logback.kafka.keyprovider

import ch.qos.logback.classic.spi.ILoggingEvent
import io.mockk.mockk
import org.amshove.kluent.shouldBeNull
import org.junit.jupiter.api.Test

class NullKeyProviderTest: AbstractKeyProviderTest() {

    override val keyProvider = NullKeyProvider()

    @Test
    fun `get null key with null`() {
        keyProvider.get(null).shouldBeNull()
    }

    @Test
    fun `get null key with any event`() {
        keyProvider.get("value").shouldBeNull()
    }

    @Test
    fun `get null key with logging event`() {
        val mockEvent = mockk<ILoggingEvent>(relaxed = true)
        keyProvider.get(mockEvent).shouldBeNull()
    }
}
