package io.bluetape4k.io.compressor

import io.bluetape4k.junit5.faker.Fakers
import io.bluetape4k.junit5.random.RandomizedTest
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.support.emptyByteArray
import io.bluetape4k.support.replicate
import io.bluetape4k.support.toUtf8Bytes
import org.amshove.kluent.shouldBeEmpty
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeGreaterThan
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.Test
import org.xerial.snappy.Snappy

@RandomizedTest
abstract class AbstractCompressorTest {

    companion object: KLogging() {
        const val REPEAT_SIZE = 5

        @JvmStatic
        protected val faker = Fakers.faker

        init {
            // Snappy 는 이렇게 한 번 초기화 해주어야 제대로 성능을 알 수 있다.
            Snappy.cleanUp()
            val compressed = Snappy.compress("bluetape4k")
            Snappy.uncompress(compressed)
        }

        fun getRandomString(): String {
            return Fakers
                .randomString(4096, 8192)
                .replicate(4)
        }
    }

    abstract val compressor: Compressor

    @Test
    fun `compress null or empty`() {
        compressor.compress(null).shouldBeEmpty()
        compressor.compress(emptyByteArray).shouldBeEmpty()
    }

    @Test
    fun `decompress null or empty`() {
        compressor.decompress(null).shouldBeEmpty()
        compressor.decompress(emptyByteArray).shouldBeEmpty()
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `compress as string`() {
        val expected = getRandomString()

        val compressed = compressor.compress(expected)
        val actual = compressor.decompress(compressed)

        log.debug { "${compressor.javaClass.simpleName} ratio=${compressed.length * 100.0 / expected.length}" }
        actual shouldBeEqualTo expected
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `compress byte array`() {
        val expected = getRandomString().toUtf8Bytes()

        val compressed = compressor.compress(expected)
        compressed.size shouldBeGreaterThan 0
        val actual = compressor.decompress(compressed)
        actual.size shouldBeGreaterThan 0

        log.debug { "${compressor.javaClass.simpleName} ratio=${compressed.size * 100.0 / expected.size}" }
        actual shouldBeEqualTo expected
    }
}
