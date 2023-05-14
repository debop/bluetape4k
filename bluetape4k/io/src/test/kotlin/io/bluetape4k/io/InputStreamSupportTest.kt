package io.bluetape4k.io

import io.bluetape4k.junit5.faker.Fakers
import io.bluetape4k.junit5.random.RandomValue
import io.bluetape4k.junit5.random.RandomizedTest
import io.bluetape4k.logging.KLogging
import io.bluetape4k.support.toUtf8Bytes
import io.bluetape4k.support.toUtf8String
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.RepeatedTest
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.StringReader
import java.io.StringWriter
import java.nio.ByteBuffer
import java.nio.channels.Channels

@RandomizedTest
class InputStreamExtensionsTest {

    companion object: KLogging() {
        private const val REPEAT_SIZE = 3

        @JvmStatic
        private val faker = Fakers.faker

        @JvmStatic
        private fun randomString() = Fakers.randomString(2048, 4096)
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `input stream copy to writer with string`() {
        val expected = randomString()

        StringWriter(DEFAULT_BUFFER_SIZE).use { writer ->
            expected.toInputStream().use { bis ->
                bis.copyTo(writer, bufferSize = 1024)

                writer.flush()
                writer.toString() shouldBeEqualTo expected
            }
        }
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `input stream copy to output stream with byte array`(@RandomValue expected: ByteArray) {
        ByteArrayOutputStream().use { bos ->
            expected.toInputStream().use { bis ->
                bis.copyTo(bos, 1024)

                bos.flush()
                bos.toByteArray() shouldBeEqualTo expected
            }
        }
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `input stream copy to output stream with string`() {
        val expected = randomString()

        ByteArrayOutputStream().use { bos ->
            expected.toInputStream().use { bis ->
                bis.copyTo(bos, 1024)

                bos.flush()
                bos.toByteArray().toUtf8String() shouldBeEqualTo expected
            }
        }
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `copyTo operator for ReadableByteChannle`() {
        val expected = randomString()

        Channels.newChannel(expected.toUtf8Bytes().toInputStream()).use { readable ->
            ByteArrayOutputStream().use { bos ->
                Channels.newChannel(bos).use { writable ->
                    readable.copyTo(writable, DEFAULT_BUFFER_SIZE)
                }
                bos.toByteArray().toUtf8String() shouldBeEqualTo expected
            }
        }
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `copyTo operator for Reader`() {
        val expected = randomString()

        StringReader(expected).use { reader ->
            StringWriter().use { writer ->
                reader.copyTo(writer)

                writer.toString() shouldBeEqualTo expected
            }
        }
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `copyTo operator for Reader to OutputStream`() {
        val expected = randomString()

        StringReader(expected).use { reader ->
            ByteArrayOutputStream().use { bos ->
                reader.copyTo(bos)
                bos.toByteArray().toUtf8String() shouldBeEqualTo expected
            }
        }
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `bytearray to input stream`() {
        val expected = randomString().toUtf8Bytes()

        expected.toInputStream().use { bis ->
            ByteArrayOutputStream().use { bos ->
                bis.copyTo(bos)

                bos.toByteArray() shouldBeEqualTo expected
            }
        }
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `string to input stream`() {
        val expected = randomString()

        expected.toInputStream().use { bis ->
            ByteArrayOutputStream().use { bos ->
                bis.copyTo(bos)

                bos.toByteArray().toUtf8String() shouldBeEqualTo expected
            }
        }
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `input stream to output stream`() {
        val expected = randomString()

        expected.toInputStream().toOutputStream().use { bos ->
            bos.toByteArray().toUtf8String() shouldBeEqualTo expected
        }
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `string to output stream`() {
        val expected = randomString()

        expected.toOutputStream().use { bos ->
            bos.toByteArray().toUtf8String() shouldBeEqualTo expected
        }
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `get available bytes from input stream`() {
        val expected = randomString()
        expected.toInputStream().use { bis ->
            bis.availableBytes().toUtf8String() shouldBeEqualTo expected
        }
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `input stream to byte array`() {
        val expected = randomString().toUtf8Bytes()

        ByteArrayInputStream(expected).use { bis ->
            bis.toByteArray() shouldBeEqualTo expected
        }
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `input stream to byte buffer`() {
        val expected = randomString().toUtf8Bytes()

        ByteArrayInputStream(expected).use { bis ->
            bis.toByteBuffer().getAllBytes() shouldBeEqualTo expected
        }
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `input stream to string`() {
        val expected = randomString()

        expected.toInputStream().use { bis ->
            bis.toUtf8String() shouldBeEqualTo expected
        }
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `input stream to string list`() {
        val expected = randomString()

        expected.toInputStream().use { bis ->
            bis.toUtf8StringList() shouldBeEqualTo expected.lines()
        }
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `input stream to line sequence`() {
        val expected = randomString()

        expected.toInputStream().use { bis ->
            bis.toUtf8LineSequence().toList() shouldBeEqualTo expected.lineSequence().toList()
        }
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `input stream put to byte buffer`() {
        val expected = randomString()
        val buffer = ByteBuffer.allocate(expected.length)

        inputStreamPutToByteBuffer(expected, buffer)
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `input stream put to byte buffer in directly`() {
        val expected = randomString()
        val buffer = ByteBuffer.allocateDirect(expected.length)

        inputStreamPutToByteBuffer(expected, buffer)
    }

    private fun inputStreamPutToByteBuffer(expected: String, buffer: ByteBuffer) {
        expected.toInputStream().buffered(DEFAULT_BUFFER_SIZE).use { bis ->
            bis.putTo(buffer)

            buffer.flip()
            buffer.getAllBytes().toUtf8String() shouldBeEqualTo expected
        }
    }
}
