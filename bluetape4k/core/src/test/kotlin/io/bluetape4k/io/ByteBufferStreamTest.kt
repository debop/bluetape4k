package io.bluetape4k.io

import io.bluetape4k.junit5.faker.Fakers
import io.bluetape4k.logging.KLogging
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.RepeatedTest

class ByteBufferStreamTest {

    companion object: KLogging() {
        private const val REPEAT_COUNT = 3

        @JvmStatic
        private val faker = Fakers.faker

        @JvmStatic
        val randomBytes: ByteArray by lazy { faker.random().nextRandomBytes(1024 * 16) }
    }

    @RepeatedTest(REPEAT_COUNT)
    fun `use ByteBufferInputStream`() {
        val bytes = randomBytes
        val buffer = bytes.toByteBuffer()

        ByteBufferInputStream(bytes).use { inputStream ->
            inputStream.available() shouldBeEqualTo bytes.size

            val actual = ByteArray(bytes.size)
            inputStream.read(actual)

            actual shouldBeEqualTo bytes
        }
    }

    @RepeatedTest(REPEAT_COUNT)
    fun `use ByteBufferOutputStream`() {
        val bytes = randomBytes

        ByteBufferOutputStream(bytes.size).use { outputStream ->
            outputStream.write(bytes)
            outputStream.flush()

            outputStream.toByteArray() shouldBeEqualTo bytes
        }
    }

    @RepeatedTest(REPEAT_COUNT)
    fun `use ByteBufferInputStream Direct`() {
        val bytes = randomBytes
        ByteBufferInputStream.direct(bytes).use { inputStream ->
            inputStream.available() shouldBeEqualTo bytes.size

            val actual = ByteArray(bytes.size)
            inputStream.read(actual)

            actual shouldBeEqualTo bytes
        }
    }

    @RepeatedTest(REPEAT_COUNT)
    fun `use ByteBufferOutputStream Direct`() {
        val bytes = randomBytes
        ByteBufferOutputStream.direct(bytes.size).use { outputStream ->
            outputStream.write(bytes)
            outputStream.flush()

            outputStream.toByteArray() shouldBeEqualTo bytes
        }
    }
}
