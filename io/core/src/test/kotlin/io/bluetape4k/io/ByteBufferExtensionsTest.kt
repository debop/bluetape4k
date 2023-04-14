package io.bluetape4k.io

import io.bluetape4k.junit5.random.RandomValue
import io.bluetape4k.junit5.random.RandomizedTest
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.trace
import io.bluetape4k.support.emptyByteArray
import io.bluetape4k.support.toUtf8ByteArray
import io.bluetape4k.support.toUtf8String
import java.nio.ByteBuffer
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeFalse
import org.amshove.kluent.shouldBeTrue
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.Test

@RandomizedTest
class ByteBufferExtensionsTest {

    companion object : KLogging() {
        private const val REPEAT_SIZE = 3
        private const val BUFFER_SIZE = 1024
    }

    @Test
    fun `isEmpty operator`() {
        val buffer = ByteBuffer.allocate(BUFFER_SIZE)
        buffer.hasRemaining().shouldBeTrue()
        buffer.limit() shouldBeEqualTo BUFFER_SIZE

        buffer.position(buffer.limit())
        buffer.remaining() shouldBeEqualTo 0
        buffer.hasRemaining().shouldBeFalse()
    }

    @Test
    fun `getAllBytes with empty bytebuffer`() {
        val buffer = ByteBuffer.wrap(emptyByteArray)
        buffer.getAllBytes() shouldBeEqualTo emptyByteArray
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `getAllBytes operator`(@RandomValue bytes: ByteArray) {
        val buffer = ByteBuffer.wrap(bytes)

        buffer.position() shouldBeEqualTo 0
        buffer.limit() shouldBeEqualTo bytes.size
        buffer.getAllBytes() shouldBeEqualTo bytes
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `put to dest byte byffer`(@RandomValue bytes: ByteArray) {
        val srcBuffer = ByteBuffer.wrap(bytes)
        val destBuffer = ByteBuffer.allocate(bytes.size)
        srcBuffer.putTo(destBuffer)

        if (destBuffer.position() > 0) {
            destBuffer.flip()
        }
        destBuffer.getAllBytes() shouldBeEqualTo bytes
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `getString operator`(@RandomValue expected: String) {
        val buffer = ByteBuffer.wrap(expected.toUtf8ByteArray())

        val actual = buffer.getString()
        actual shouldBeEqualTo expected
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `copy operator`(@RandomValue expected: ByteArray) {
        val copied = expected.toByteBuffer().copy()
        copied.getAllBytes() shouldBeEqualTo expected
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `ByteArray to byteBuffer Direct`(@RandomValue expected: ByteArray) {
        val buffer = expected.toByteBufferDirect()
        buffer.getAllBytes() shouldBeEqualTo expected
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `convert to hexString`(@RandomValue expected: String) {
        val buffer = ByteBuffer.wrap(expected.toUtf8ByteArray())
        val hexStr = buffer.encodeHexString()
        log.trace { "Hex string=$hexStr" }

        val decodedBuffer = hexStr.decodeHexByteBuffer()
        val actual = decodedBuffer.getBytes().toUtf8String()
        actual shouldBeEqualTo expected
    }
}
