package io.bluetape4k.collections

import io.bluetape4k.collections.eclipse.primitives.intArrayListOf
import io.bluetape4k.collections.eclipse.primitives.toIntArrayList
import io.bluetape4k.logging.KLogging
import io.bluetape4k.support.emptyIntArray
import io.bluetape4k.support.emptyLongArray
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeLessOrEqualTo
import org.amshove.kluent.shouldContainSame
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import kotlin.test.assertFailsWith

@Suppress("DEPRECATION")
class BatchSequenceTest {

    companion object : KLogging()

    @Test
    fun `batch sequence with batch size 2`() {
        var count = 0
        (1..10).batch(2).forEach { group ->
            count++
            if (count == 5) {
                group.asIterable() shouldContainSame listOf(9, 10)
            } else {
                group.asIterable().size() shouldBeEqualTo 2
            }
        }
        count shouldBeEqualTo 5
    }

    @ParameterizedTest(name = "batch size {0}")
    @ValueSource(ints = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10])
    fun `batch sequence with batch size`(batchSize: Int) {
        val max = 1000
        var count = 0
        (1..max).batch(batchSize).forEach { group ->
            group.count() shouldBeLessOrEqualTo batchSize
            count++
        }
        val reminder = if (max % batchSize == 0) 0 else 1
        count shouldBeEqualTo (max / batchSize) + reminder
    }

    @Test
    fun `batch sequence grouping with 2`() {
        val expected = listOf(
            intArrayListOf(1, 2),
            intArrayListOf(3, 4),
            intArrayListOf(5, 6),
            intArrayListOf(7, 8),
            intArrayListOf(9, 10)
        )

        var count = 0
        (1..10).batch(2).forEach { group ->
            val array = group.toIntArrayList()
            array shouldBeEqualTo expected[count++]
        }
    }

    @Test
    fun `batch sequence grouping with 3`() {

        val expected = listOf(
            listOf(1, 2, 3),
            listOf(4, 5, 6),
            listOf(7, 8, 9),
            listOf(10)
        )
        var groupCount = 0
        (1..10).batch(3).forEachIndexed { index, group ->
            groupCount++
            group.asIterable() shouldContainSame expected[index]
        }
        groupCount shouldBeEqualTo 4
    }

    @Test
    fun `grouping in even size`() {
        verifyAsStream((1..15).toList(), 5, 3)
    }

    @Test
    fun `grouping in odd size`() {
        verifyAsStream((1..18).toList(), 5, 4)
    }

    @Test
    fun `elements less than groupSize`() {
        verifyAsStream(listOf(1, 3, 5), 5, 1)
    }

    @Test
    fun `batch size is 1`() {
        verifyAsStream(listOf(1, 3, 5), 1, 3)
    }

    @ParameterizedTest(name = "batch size {0}")
    @ValueSource(ints = [-100, -10, -1, 0])
    fun `batch size is invalid number`(batchSize: Int) {
        assertFailsWith<AssertionError> {
            listOf(1, 3, 5).batch(batchSize).toList()
        }
    }

    @Test
    fun `grouping empty sequence`() {
        emptyIntArray.asSequence().batch(1).forEach { fail("호출되면 안됩니다") }
        emptyLongArray.asSequence().batch(1).forEach { fail("호출되면 안됩니다") }
    }

    private fun verifyAsStream(targetList: List<Int>, batchSize: Int, expectedGroupSize: Int) {
        run {
            var groupSize = 0
            val items = intArrayListOf()

            targetList.batch(batchSize).forEach { group ->
                groupSize++
                group.forEach { items.add(it) }
            }
            groupSize shouldBeEqualTo expectedGroupSize
            items shouldBeEqualTo targetList.toIntArrayList()
        }

        run {
            var groupSize = 0
            val items = intArrayListOf()

            targetList.batch(batchSize).forEach { group ->
                groupSize++
                group.forEach { items.add(it) }
            }
            groupSize shouldBeEqualTo expectedGroupSize
            items shouldBeEqualTo targetList.toIntArrayList()
        }
    }
}
