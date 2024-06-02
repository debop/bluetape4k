package io.bluetape4k.tiktoken

import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldNotBeEqualTo
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

class ImmutableByteArrayTest {

    @Test
    fun `can be used as key in map`() {
        val text = "1, 2, 3"
        val key1 = ImmutableByteArray(text)
        val key2 = ImmutableByteArray(text)

        key2 shouldBeEqualTo key1
        key2.hashCode() shouldBeEqualTo key1.hashCode()
        key2.toString() shouldBeEqualTo key2.toString()
    }

    @Test
    fun `can not be mutated when using byte array constructor`() {
        val bytes = byteArrayOf(1, 2, 3)
        val byteArray = ImmutableByteArray(bytes)

        bytes[0] = 4
        ImmutableByteArray(bytes) shouldNotBeEqualTo byteArray
        ImmutableByteArray(byteArrayOf(1, 2, 3)) shouldBeEqualTo byteArray
    }

    @Test
    fun `can not be mutated when using get raw array`() {
        val text = "1, 2, 3"
        val byteArray = ImmutableByteArray(text)

        val modifiedArray = byteArray.getRawArray()
        modifiedArray[0] = 4

        ImmutableByteArray(modifiedArray) shouldNotBeEqualTo byteArray
        ImmutableByteArray(text) shouldBeEqualTo byteArray
    }

    @Test
    fun `get byte array size`() {
        val text = "1, 2, 3"
        val byteArray = ImmutableByteArray(text)
        byteArray.size shouldBeEqualTo text.length
    }

    @Test
    fun `get bytes between returns correct slice of array`() {
        val byteArray = ImmutableByteArray(byteArrayOf(1, 2, 3, 4, 5, 6))
        byteArray.getBytesBetween(3, 6) shouldBeEqualTo ImmutableByteArray(byteArrayOf(4, 5, 6))
    }

    @Test
    fun `get bytes between throws when inclusive start index out of bounds`() {
        val byteArray = ImmutableByteArray(byteArrayOf(1, 2, 3, 4, 5, 6))

        assertFailsWith<IllegalArgumentException> {
            byteArray.getBytesBetween(-1, 6)
        }
        assertFailsWith<IllegalArgumentException> {
            byteArray.getBytesBetween(9, 10)
        }
    }

    @Test
    fun `get bytes between throws when exclusive end index out of bounds`() {
        val byteArray = ImmutableByteArray(byteArrayOf(1, 2, 3, 4, 5, 6))

        assertFailsWith<IllegalArgumentException> {
            byteArray.getBytesBetween(0, 7)
        }
        assertFailsWith<IllegalArgumentException> {
            byteArray.getBytesBetween(0, -1)
        }
    }

    @Test
    fun `get bytes between throws when start index is greater than end index`() {
        val byteArray = ImmutableByteArray(byteArrayOf(1, 2, 3, 4, 5, 6))

        assertFailsWith<IllegalArgumentException> {
            byteArray.getBytesBetween(3, 2)
        }
    }
}
