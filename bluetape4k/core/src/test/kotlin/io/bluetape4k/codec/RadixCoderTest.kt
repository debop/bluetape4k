package io.bluetape4k.codec

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.trace
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import kotlin.random.Random
import kotlin.test.assertFailsWith

class RadixCoderTest {

    companion object: KLogging()

    fun getU8() = RadixCoders.U8
    fun getU16() = RadixCoders.U16
    fun getAll() = RadixCoders.ALL

    @Test
    fun `equality of RadixCoder U8`() {
        (2..256).forEach {
            RadixCoder.u8(it) shouldBeEqualTo RadixCoder.u8(it)
        }
    }

    @Test
    fun `equality of RadixCoder U16`() {
        (2..256).forEach {
            RadixCoder.u16(it) shouldBeEqualTo RadixCoder.u16(it)
        }
    }

    @Test
    fun `invalid small base constructor`() {
        assertFailsWith<IllegalArgumentException> {
            RadixCoder.u8(0)
        }
        assertFailsWith<IllegalArgumentException> {
            RadixCoder.u8(1)
        }
        assertFailsWith<IllegalArgumentException> {
            RadixCoder.u16(0)
        }
        assertFailsWith<IllegalArgumentException> {
            RadixCoder.u16(1)
        }
    }

    @Test
    fun `invalid big base constructor`() {
        assertFailsWith<IllegalArgumentException> {
            RadixCoder.u8(0x101)
        }
        assertFailsWith<IllegalArgumentException> {
            RadixCoder.u16(0x10001)
        }
    }

    @Test
    fun `decode out of range`() {
        assertFailsWith<IllegalArgumentException> {
            RadixCoder.u8(64).decode(byteArrayOf(64))
        }
        assertFailsWith<IllegalArgumentException> {
            RadixCoder.u8(200).decode(byteArrayOf(-1))
        }

        assertFailsWith<IllegalArgumentException> {
            RadixCoder.u16(64).decode(shortArrayOf(64))
        }
        assertFailsWith<IllegalArgumentException> {
            RadixCoder.u16(30000).decode(shortArrayOf(-1))
        }
    }

    private fun <N> invert(coder: RadixCoder<N>, bytes: ByteArray) {
        log.trace { "bytes=${bytes.contentToString()}" }
        val encoded = coder.encode(bytes)
        log.trace { "encoded=$encoded" }
        val decoded = coder.decode(encoded)
        log.trace { "decoded=${decoded.contentToString()}" }
        decoded shouldBeEqualTo bytes
    }

    @ParameterizedTest(name = "encode/decode zero filled array U8 {0}")
    @MethodSource("getU8")
    fun `invert zero filled U8 RadixCoder`(coder: RadixCoder<*>) {
        for (i in 0..65) {
            invert(coder, ByteArray(i))
        }
    }

    @ParameterizedTest(name = "encode/decode zero filled array U16 {0}")
    @MethodSource("getU16")
    fun `invert zero filled U16 RadixCoder`(coder: RadixCoder<*>) {
        for (i in 0..65) {
            invert(coder, ByteArray(i))
        }
    }

    @Test
    fun `invert random`() {
        RadixCoders.ALL.forEach { coder ->
            for (i in 0 until 5) {
                val bytes = Random.nextBytes(2 + Random.nextInt(300))
                invert(coder, bytes)
            }
        }
    }

    @ParameterizedTest(name = "byte range coder={0}")
    @MethodSource("getAll")
    fun `invert all length 1`(coder: RadixCoder<*>) {
        TestBytes.allLength1().forEach { bytes ->
            invert(coder, bytes)
        }
    }

    @ParameterizedTest(name = "short range coder={0}")
    @MethodSource("getAll")
    fun `invert all length 2`(coder: RadixCoder<*>) {
        TestBytes.allLength2().forEach { bytes ->
            invert(coder, bytes)
        }
    }
}
