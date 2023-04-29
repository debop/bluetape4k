package io.bluetape4k.io

import io.bluetape4k.junit5.random.RandomValue
import io.bluetape4k.junit5.random.RandomizedTest
import io.bluetape4k.logging.KLogging
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.RepeatedTest

@RandomizedTest
class ByteBufferStreamTest {

    companion object : KLogging() {
        private const val REPEAT_COUNT = 10
    }

    @RepeatedTest(REPEAT_COUNT)
    fun `use ByteBufferInputStream`(@RandomValue bytes: ByteArray) {
        val buffer = bytes.toByteBuffer()

        ByteBufferInputStream(buffer).use { inputStream ->
            inputStream.available() shouldBeEqualTo bytes.size

            val actual = ByteArray(bytes.size)
            inputStream.read(actual)

            actual shouldBeEqualTo bytes
        }
    }

    @RepeatedTest(REPEAT_COUNT)
    fun `use ByteBufferOutputStream`(@RandomValue bytes: ByteArray) {
        val buffer = bytes.toByteBuffer()

        ByteBufferOutputStream(buffer).use { outputStream ->
            outputStream.write(bytes)
            outputStream.flush()

            outputStream.toByteArray() shouldBeEqualTo bytes
        }
    }
}
