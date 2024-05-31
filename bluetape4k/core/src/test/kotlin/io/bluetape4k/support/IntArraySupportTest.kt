package io.bluetape4k.support

import io.bluetape4k.logging.KLogging
import org.amshove.kluent.internal.assertFailsWith
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test

class IntArraySupportTest {

    companion object: KLogging() {
        private const val REPEAT_SIZE = 5
    }

    @Test
    fun `index of int`() {
        val ints = intArrayOf(1, 2, 3, 4, 5)
        val target = 3

        ints.indexOf(target, 0, ints.size) shouldBeEqualTo 2

        assertFailsWith<IllegalArgumentException> {
            ints.indexOf(target, -1, 1)
        }

        assertFailsWith<IllegalArgumentException> {
            ints.indexOf(target, 1, ints.size + 1)
        }
    }

    @Test
    fun `index of double array`() {
        val ints = intArrayOf(1, 2, 3, 4, 5)
        val target = intArrayOf(3, 4)

        ints.indexOf(target, 0, ints.size) shouldBeEqualTo 2

        assertFailsWith<IllegalArgumentException> {
            ints.indexOf(target, -1, 1)
        }

        assertFailsWith<IllegalArgumentException> {
            ints.indexOf(target, 1, ints.size + 1)
        }
    }

    @Test
    fun `ensure capacity`() {
        val ints = intArrayOf(1, 2, 3, 4, 5)

        ints.ensureCapacity(ints.size, 5) shouldBeEqualTo intArrayOf(1, 2, 3, 4, 5)
        ints.ensureCapacity(10, 0) shouldBeEqualTo intArrayOf(
            1, 2, 3, 4, 5,
            0, 0, 0, 0, 0
        )

        assertFailsWith<IllegalArgumentException> {
            ints.ensureCapacity(-1, 0)
        }

        assertFailsWith<IllegalArgumentException> {
            ints.ensureCapacity(0, -1)
        }
    }

    @Test
    fun `concat double arrays`() {
        val ints1 = intArrayOf(1, 2, 3)
        val ints2 = intArrayOf(4, 5)

        concat(ints1, ints2) shouldBeEqualTo intArrayOf(1, 2, 3, 4, 5)
        concat(ints2, ints1) shouldBeEqualTo intArrayOf(4, 5, 1, 2, 3)
    }

    @Test
    fun `reverse double array`() {
        val ints = intArrayOf(1, 2, 3, 4, 5)

        ints.reverseTo(0, ints.size) shouldBeEqualTo intArrayOf(5, 4, 3, 2, 1)
        ints.reverseTo(1, 4) shouldBeEqualTo intArrayOf(1, 4, 3, 2, 5)
    }

    @Test
    fun `reverse current double array`() {
        val ints = intArrayOf(1, 2, 3, 4, 5)
        ints.reverse()
        ints shouldBeEqualTo intArrayOf(5, 4, 3, 2, 1)

        val ints2 = intArrayOf(1, 2, 3, 4, 5)
        ints2.reverse(1, 4)
        ints2 shouldBeEqualTo intArrayOf(1, 4, 3, 2, 5)
    }

    @Test
    fun `rotate double array elements`() {
        val ints = intArrayOf(1, 2, 3, 4, 5)
        ints.rotateTo(2) shouldBeEqualTo intArrayOf(4, 5, 1, 2, 3)
        ints.rotateTo(-2) shouldBeEqualTo intArrayOf(3, 4, 5, 1, 2)
    }

    @Test
    fun `rotate itself double array elements`() {
        val ints = intArrayOf(1, 2, 3, 4, 5)
        ints.rotate(2)
        ints shouldBeEqualTo intArrayOf(4, 5, 1, 2, 3)

        val ints2 = intArrayOf(1, 2, 3, 4, 5)
        ints2.rotate(-2)
        ints2 shouldBeEqualTo intArrayOf(3, 4, 5, 1, 2)
    }
}
