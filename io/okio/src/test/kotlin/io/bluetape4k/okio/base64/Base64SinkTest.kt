package io.bluetape4k.okio.base64

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.okio.AbstractOkioTest
import io.bluetape4k.okio.bufferOf
import io.bluetape4k.okio.byteStringOf
import okio.Buffer
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.Test

class Base64SinkTest: AbstractOkioTest() {

    companion object: KLogging() {
        private const val REPEAT_SIZE = 5
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `write fixed string`() {
        val output = Buffer()
        val sink = Base64Sink(output)

        val expected = faker.lorem().paragraph()
        val source = bufferOf(expected)
        sink.write(source, Long.MAX_VALUE)

        // NOTE: okio의 Base64 인코딩이 apache commons codec의 Base64 인코딩과 다르다.
        output.readUtf8() shouldBeEqualTo byteStringOf(expected).base64()
    }

    @Test
    fun `write partial string`() {
        val output = Buffer()
        val sink = Base64Sink(output)

        val expected = "okio oh my¿¡"
        log.debug { "Encoded string: $expected" }

        val source = bufferOf(expected)
        sink.write(source, 5)

        val encoded = output.readUtf8()
        log.debug { "Encoded data: $encoded" }
        encoded shouldBeEqualTo byteStringOf(expected.substring(0, 5)).base64()
    }
}
