package io.bluetape4k.io.compressor

import io.bluetape4k.junit5.faker.Fakers
import io.bluetape4k.junit5.random.RandomizedTest
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.support.emptyByteArray
import io.bluetape4k.support.replicate
import io.bluetape4k.support.toUtf8Bytes
import io.bluetape4k.support.toUtf8String
import net.datafaker.Faker
import org.amshove.kluent.shouldBeEmpty
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.Test
import org.xerial.snappy.Snappy

@RandomizedTest
abstract class AbstractCompressorTest {

    companion object : KLogging() {
        const val TEST_COUNT = 10

        private val faker = Faker()

        init {
            Snappy.cleanUp()
            val compressed = Snappy.compress("debop")
            Snappy.uncompress(compressed)
        }

        fun getRandomString(): String =
            Fakers
                .randomString(4096, 8192 * 8, true)
                .replicate(4)
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

    @RepeatedTest(TEST_COUNT)
    fun `compress as string`() {
        val expected = getRandomString()

        val compressed = compressor.compress(expected)
        val actual = compressor.decompress(compressed)

        actual shouldBeEqualTo expected
    }

    @RepeatedTest(TEST_COUNT)
    fun `compress plain string`() {
        val expected = getRandomString()

        val compressed = compressor.compress(expected.toUtf8Bytes())
        val actual = compressor.decompress(compressed).toUtf8String()

        log.debug { "Ratio=${compressed.size * 100.0 / expected.length}" }

        actual shouldBeEqualTo expected
    }

    @RepeatedTest(TEST_COUNT)
    fun `compress byte array`() {
        val expected = getRandomString().toUtf8Bytes()

        val compressed = compressor.compress(expected)
        val actual = compressor.decompress(compressed)

        log.debug { "Ratio=${compressed.size * 100.0 / expected.size}" }

        actual shouldBeEqualTo expected
    }
}
