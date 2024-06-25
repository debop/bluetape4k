package io.bluetape4k.okio.base64

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.okio.AbstractOkioTest
import io.bluetape4k.okio.bufferOf
import io.bluetape4k.support.toUtf8Bytes
import okio.Buffer
import okio.Sink
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.RepeatedTest

abstract class AbstractBaseNSinkTest: AbstractOkioTest() {

    companion object: KLogging() {
        private const val REPEAT_SIZE = 5
    }

    protected abstract fun createSink(delegate: Sink): Sink
    protected abstract fun getEncodedString(expectedBytes: ByteArray): String


    @RepeatedTest(REPEAT_SIZE)
    fun `write fixed string`() {
        val output = Buffer()
        val sink = createSink(output)

        val expected = faker.lorem().paragraph()
        log.debug { "Plain string: $expected" }

        val source = bufferOf(expected)
        sink.write(source, source.size)

        val encoded = output.readUtf8()
        log.debug { "Encoded data: $encoded" }
        encoded shouldBeEqualTo getEncodedString(expected.toUtf8Bytes())
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `write partial string`() {
        val output = Buffer()
        val sink = createSink(output)

        val expected = faker.lorem().paragraph()
        log.debug { "Encoded string: $expected" }

        val source = bufferOf(expected)
        sink.write(source, 5)

        val encoded = output.readUtf8()
        log.debug { "Encoded data: $encoded" }
        encoded shouldBeEqualTo getEncodedString(expected.toUtf8Bytes().copyOfRange(0, 5))
    }
}
