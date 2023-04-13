package io.bluetape4k.ahocorasick.interval

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldContainSame
import org.junit.jupiter.api.Test

class IntervalTreeTest {

    companion object : KLogging()

    @Test
    fun `find overlaps`() {
        val intervals = List(6) { Interval(it, it + 2) }
        val tree = IntervalTree(intervals)

        val overlaps = tree.findOverlaps(Interval(1, 3))
        overlaps.size shouldBeEqualTo 3

        overlaps[0] shouldBeEqualTo Interval(2, 4)
        overlaps[1] shouldBeEqualTo Interval(3, 5)
        overlaps[2] shouldBeEqualTo Interval(0, 2)
    }

    @Test
    fun `find overlaps with various size`() {
        val intervals = listOf(
            Interval(0, 2),
            Interval(4, 5),
            Interval(2, 10),
            Interval(6, 13),
            Interval(9, 15),
            Interval(12, 16)
        )
        val tree = IntervalTree(intervals)

        tree.findOverlaps(Interval(0, 2)) shouldBeEqualTo listOf(Interval(2, 10))
        tree.findOverlaps(Interval(4, 5)) shouldBeEqualTo listOf(Interval(2, 10))

        tree.findOverlaps(Interval(2, 10)) shouldContainSame listOf(
            Interval(0, 2),
            Interval(4, 5),
            Interval(6, 13),
            Interval(9, 15)
        )
        tree.findOverlaps(Interval(6, 13)) shouldContainSame listOf(Interval(2, 10), Interval(9, 15), Interval(12, 16))
        tree.findOverlaps(Interval(9, 15)) shouldContainSame listOf(Interval(2, 10), Interval(6, 13), Interval(12, 16))
        tree.findOverlaps(Interval(12, 16)) shouldContainSame listOf(Interval(6, 13), Interval(9, 15))
    }

    @Test
    fun `remove overlap`() {
        val intervals = listOf(
            Interval(0, 2),
            Interval(4, 5),
            Interval(2, 10),
            Interval(6, 13),
            Interval(9, 15),
            Interval(12, 16)
        )
        val tree = IntervalTree(intervals)

        val removed = tree.removeOverlaps(intervals.toMutableList())
        log.debug { "removed overlaps=$removed" }
        removed.size shouldBeEqualTo 2
        removed shouldBeEqualTo listOf(Interval(2, 10), Interval(12, 16))
    }

    private fun assertOverlaps(interval: Intervalable, expectedStart: Int, expectedEnd: Int) {
        interval.start shouldBeEqualTo expectedStart
        interval.end shouldBeEqualTo expectedEnd
    }
}
