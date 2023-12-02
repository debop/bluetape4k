package io.bluetape4k.logback.kafka.keyprovider

import io.bluetape4k.logback.kafka.utils.hashBytes
import org.amshove.kluent.shouldContainSame
import org.junit.jupiter.api.Test

class ThreadNameKeyProviderTest: AbstractKeyProviderTest() {

    override val keyProvider = ThreadNameKeyProvider()

    @Test
    fun `thread name 기반으로 key 값 생성하기`() {
        val threadName = Thread.currentThread().name
        keyProvider.get(sampleEvent)!! shouldContainSame threadName.hashBytes()!!
    }
}
