package io.bluetape4k.codec

import io.bluetape4k.junit5.faker.Fakers
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.support.toUtf8Bytes
import io.bluetape4k.support.toUtf8String
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.*
import kotlin.random.Random
import kotlin.test.assertFailsWith

class AsciiRadixCoderTest {

    companion object: KLogging() {
        private const val REPEAT_SIZE = 10
        val faker = Fakers.faker
    }

    fun getAscii36() = AsciiRadixCoders.ASCII36
    fun getAscii58() = AsciiRadixCoders.ASCII58
    fun getAll() = AsciiRadixCoders.ALL

    @Test
    fun `equality with AsciiRadixCoder`() {
        AsciiRadixCoder("12") shouldBeEqualTo AsciiRadixCoder("12")
        AsciiRadixCoder("21") shouldBeEqualTo AsciiRadixCoder("21")
        AsciiRadixCoder("123") shouldBeEqualTo AsciiRadixCoder("123")
        AsciiRadixCoder("123a") shouldBeEqualTo AsciiRadixCoder("123a")

        repeat(10) {
            val base = Fakers.alphaNumericString("#?")
            AsciiRadixCoder(base) shouldBeEqualTo AsciiRadixCoder(base)
        }
    }

    @Test
    fun `empty constructor`() {
        assertFailsWith<IllegalArgumentException> {
            AsciiRadixCoder("")
        }
    }

    @Test
    fun `one char constructor`() {
        assertFailsWith<IllegalArgumentException> {
            AsciiRadixCoder("A")
        }
    }

    @Test
    fun `duplicated char constructor`() {
        assertFailsWith<IllegalArgumentException> {
            AsciiRadixCoder("01230123")
        }
    }

    @Test
    fun `invalid char decode`() {
        assertFailsWith<IllegalArgumentException> {
            AsciiRadixCoder("01").decode("010X011")
        }
    }

    private fun invert(coder: AsciiRadixCoder, bytes: ByteArray) {
        coder.decode(coder.encode(bytes)) shouldBeEqualTo bytes
    }

    @ParameterizedTest(name = "zero filled bytes. {0}")
    @MethodSource("getAll")
    fun `invert zero filled bytes`(coder: AsciiRadixCoder) {
        (0..65).forEach {
            invert(coder, ByteArray(it))
        }
    }

    @ParameterizedTest(name = "random bytes. {0}")
    @MethodSource("getAll")
    fun `invert random bytes`(coder: AsciiRadixCoder) {
        repeat(10) {
            val bytes = Random.nextBytes(2 + Random.nextInt(500))
            invert(coder, bytes)
        }
    }

    @ParameterizedTest(name = "byte range coder={0}")
    @MethodSource("getAll")
    fun `invert all length 1`(coder: AsciiRadixCoder) {
        TestBytes.allLength1().forEach { bytes ->
            invert(coder, bytes)
        }
    }

    @ParameterizedTest(name = "short range coder={0}")
    @MethodSource("getAll")
    fun `invert all length 2`(coder: AsciiRadixCoder) {
        TestBytes.allLength2().forEach { bytes ->
            invert(coder, bytes)
        }
    }

    @ParameterizedTest(name = "byte range coder 36={0}")
    @MethodSource("getAscii36")
    fun `invert all length 1 using ascii 36`(coder: AsciiRadixCoder) {
        TestBytes.allLength1().forEach { bytes ->
            invert(coder, bytes)
        }
    }

    @ParameterizedTest(name = "byte range coder 36={0}")
    @MethodSource("getAscii36")
    fun `invert all length 2 using ascii 36`(coder: AsciiRadixCoder) {
        TestBytes.allLength2()
            .filterNot { it[0] == 0.toByte() }
            .forEach { bytes ->
                invert(coder, bytes)
            }
    }

    @Disabled("시간이 많이 걸린다.")
    @ParameterizedTest(name = "byte range coder 36={0}")
    @MethodSource("getAscii36")
    fun `invert all length 3 using ascii 36`(coder: AsciiRadixCoder) {
        TestBytes.allLength3()
            .filterNot { it[0] == 0.toByte() }
            .forEach { bytes ->
                val asInt: Int =
                    bytes[0].toInt() and 0xFF shl 16 or (bytes[1].toInt() and 0xFF shl 8) or (bytes[2].toInt() and 0xFF)
                val enc0 = coder.encode(bytes)
                enc0 shouldBeEqualTo asInt.toString(coder.base())
                val dec1 = coder.decode(enc0)
                dec1 shouldBeEqualTo bytes
            }
    }

    @ParameterizedTest(name = "byte range coder 58={0}")
    @MethodSource("getAscii58")
    fun `invert all length 1 using ascii 58`(coder: AsciiRadixCoder) {
        TestBytes.allLength1().forEach { bytes ->
            invert(coder, bytes)
        }
    }

    @ParameterizedTest(name = "byte range coder 58={0}")
    @MethodSource("getAscii58")
    fun `invert all length 2 using ascii 58`(coder: AsciiRadixCoder) {
        TestBytes.allLength2()
            .forEach { bytes ->
                invert(coder, bytes)
            }
    }

    @Disabled("시간이 많이 걸린다.")
    @ParameterizedTest(name = "byte range coder 58={0}")
    @MethodSource("getAscii58")
    fun `invert all length 3 using ascii 58`(coder: AsciiRadixCoder) {
        TestBytes.allLength3()
            .filterNot { it[0] == 0.toByte() }
            .forEach { bytes ->
                val asInt: Int =
                    bytes[0].toInt() and 0xFF shl 16 or (bytes[1].toInt() and 0xFF shl 8) or (bytes[2].toInt() and 0xFF)
                val enc0 = coder.encode(bytes)
                enc0 shouldBeEqualTo asInt.toString(coder.base())
                val dec1 = coder.decode(enc0)
                dec1 shouldBeEqualTo bytes
            }
    }

    @Nested
    inner class Base58 {

        private val base58 = AsciiRadixCoder("123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz")

        @RepeatedTest(REPEAT_SIZE)
        fun `encode uuid`() {
            val original = UUID.randomUUID().toString()

            val encoded = base58.encode(original.toUtf8Bytes())
            log.debug { "original=$original, encoded=$encoded" }
            val decoded = base58.decode(encoded).toUtf8String()
            decoded shouldBeEqualTo original
        }

        @RepeatedTest(REPEAT_SIZE)
        fun `encode and decode`() {
            val original = Fakers.randomString(8, 16)

            val encoded = base58.encode(original.toUtf8Bytes())
            log.debug { "original=$original, encoded=$encoded" }
            val decoded = base58.decode(encoded).toUtf8String()
            decoded shouldBeEqualTo original
        }
    }

    @Nested
    inner class Base62 {

        private val base62 = AsciiRadixCoder("0123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz")

        @RepeatedTest(REPEAT_SIZE)
        fun `encode string`() {
            val original = Fakers.fixedString(4)

            val encoded = base62.encode(original.toUtf8Bytes())
            log.debug { "original=$original, encoded=$encoded" }
            val decoded = base62.decode(encoded).toUtf8String()
            decoded shouldBeEqualTo original
        }

        @RepeatedTest(REPEAT_SIZE)
        fun `encode int`() {
            val original = Fakers.random.nextInt(0, 1000).toString()

            val encoded = base62.encode(original.toUtf8Bytes())
            log.debug { "original=$original, encoded=$encoded" }
            val decoded = base62.decode(encoded).toUtf8String()
            decoded shouldBeEqualTo original
        }

        @RepeatedTest(REPEAT_SIZE)
        fun `encode uuid`() {
            val original = UUID.randomUUID().toString()

            val encoded = base62.encode(original.toUtf8Bytes())
            log.debug { "original=$original, encoded=$encoded" }
            val decoded = base62.decode(encoded).toUtf8String()
            decoded shouldBeEqualTo original
        }

        @RepeatedTest(REPEAT_SIZE)
        fun `encode and decode`() {
            val original = Fakers.randomString(16, 256)

            val encoded = base62.encode(original.toUtf8Bytes())
            log.debug { "original=$original, encoded=$encoded" }
            val decoded = base62.decode(encoded).toUtf8String()
            decoded shouldBeEqualTo original
        }
    }
}
