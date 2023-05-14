package io.bluetape4k.collections

import io.bluetape4k.logging.KLogging
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test

class SequenceSupportTest {

    companion object: KLogging()

    @Test
    fun `build char sequence`() {
        val sequence = charSequence('a', 'z', 2)
        val array = sequence.toCharArray()

        array.size shouldBeEqualTo sequence.count()
        sequence.forEachIndexed { index, value ->
            array[index] shouldBeEqualTo value
        }
    }

    @Test
    fun `build byte sequence`() {
        val sequence = byteSequence(1, 100, 2)
        val array = sequence.toByteArray()

        array.size shouldBeEqualTo sequence.count()
        sequence.forEachIndexed { index, value ->
            array[index] shouldBeEqualTo value
        }
    }

    @Test
    fun `build int sequence`() {
        val sequence = intSequence(1, 100, 2)
        val array = sequence.toIntArray()

        array.size shouldBeEqualTo sequence.count()
        sequence.forEachIndexed { index, value ->
            array[index] shouldBeEqualTo value
        }
    }

    @Test
    fun `build long sequence`() {
        val sequence = longSequence(1L, 100L, 2L)
        val array = sequence.toLongArray()

        array.size shouldBeEqualTo sequence.count()
        sequence.forEachIndexed { index, value ->
            array[index] shouldBeEqualTo value
        }
    }

    @Test
    fun `build float sequence`() {
        val sequence = floatSequence(1.0F, 10.0F, 0.5F)
        val array = sequence.toFloatArray()

        array.size shouldBeEqualTo sequence.count()
        sequence.forEachIndexed { index, value ->
            array[index] shouldBeEqualTo value
        }
    }

    @Test
    fun `build double sequence`() {
        val sequence = doubleSequence(1.0, 10.0, 0.5)
        val array = sequence.toDoubleArray()

        array.size shouldBeEqualTo sequence.count()
        sequence.forEachIndexed { index, value ->
            array[index] shouldBeEqualTo value
        }
    }

    @Test
    fun `sliding 하기`() {
        val list = listOf(1, 2, 3, 4)

        val sliding = list.asSequence().sliding(3, false)
        sliding.toList() shouldBeEqualTo listOf(listOf(1, 2, 3), listOf(2, 3, 4))

        val sliding2 = list.asSequence().sliding(3, true)
        sliding2.toList() shouldBeEqualTo listOf(listOf(1, 2, 3), listOf(2, 3, 4), listOf(3, 4), listOf(4))
    }
}
