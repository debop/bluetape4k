package io.bluetape4k.logback.kafka.keyprovider

import ch.qos.logback.core.CoreConstants
import io.bluetape4k.logback.kafka.utils.hashBytes
import org.amshove.kluent.shouldContainSame
import org.junit.jupiter.api.Test

class HostNameKeyProviderTest: AbstractKeyProviderTest() {

    override val keyProvider = HostNameKeyProvider()

    @Test
    fun `get key value by hostname`() {
        ctx.putProperty(CoreConstants.HOSTNAME_KEY, "localhost")
        keyProvider.context = ctx

        keyProvider.get(sampleEvent)!! shouldContainSame "localhost".hashBytes()!!
    }
}
