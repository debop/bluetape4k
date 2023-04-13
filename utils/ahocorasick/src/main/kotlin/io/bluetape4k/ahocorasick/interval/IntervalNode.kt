package io.bluetape4k.ahocorasick.interval

import io.bluetape4k.core.AbstractValueObject
import java.util.*

class IntervalNode(inputs: Iterable<Intervalable>) : AbstractValueObject() {

    enum class Direction { LEFT, RIGHT }

    var left: IntervalNode? = null
    var right: IntervalNode? = null
    val intervals: MutableList<Intervalable> = mutableListOf()
    val median = determineMedian(inputs)

    init {
        buildTree(inputs)
    }

    private fun determineMedian(inputs: Iterable<Intervalable>): Int {
        val start = inputs.map { it.start }.minOrNull() ?: 0
        val end = inputs.map { it.end }.maxOrNull() ?: 0
        return (start + end) / 2
    }

    private fun buildTree(inputs: Iterable<Intervalable>) {
        if (inputs.count() == 0) {
            return
        }
        val toLeft = LinkedList<Intervalable>()
        val toRight = LinkedList<Intervalable>()

        inputs.forEach { input ->
            when {
                input.end < median -> toLeft.add(input)
                input.start > median -> toRight.add(input)
                else -> intervals.add(input)
            }
        }
        if (toLeft.isNotEmpty()) {
            this.left = IntervalNode(toLeft)
        }
        if (toRight.isNotEmpty()) {
            this.right = IntervalNode(toRight)
        }
    }

    fun findOverlaps(interval: Intervalable): MutableList<Intervalable> {
        val overlaps = LinkedList<Intervalable>()

        when {
            interval.start > median -> {
                addToOverlaps(interval, overlaps, findOverlappingRanges(right, interval))
                addToOverlaps(interval, overlaps, checkForOverlapsToRight(interval))
            }

            interval.end < median -> {
                addToOverlaps(interval, overlaps, findOverlappingRanges(left, interval))
                addToOverlaps(interval, overlaps, checkForOverlapsToLeft(interval))
            }

            else -> {
                addToOverlaps(interval, overlaps, this.intervals)
                addToOverlaps(interval, overlaps, findOverlappingRanges(left, interval))
                addToOverlaps(interval, overlaps, findOverlappingRanges(right, interval))
            }
        }
        return overlaps
    }

    private fun addToOverlaps(
        interval: Intervalable,
        overlaps: MutableList<Intervalable>,
        newOverlaps: List<Intervalable>,
    ) {
        overlaps.addAll(newOverlaps.filter { it != interval })
    }

    private fun checkForOverlapsToLeft(interval: Intervalable): List<Intervalable> =
        checkForOverlaps(interval, Direction.LEFT)

    private fun checkForOverlapsToRight(interval: Intervalable): List<Intervalable> =
        checkForOverlaps(interval, Direction.RIGHT)


    private fun checkForOverlaps(interval: Intervalable, direction: Direction): List<Intervalable> {
        val overlaps = LinkedList<Intervalable>()

        this.intervals.forEach {
            when (direction) {
                Direction.LEFT ->
                    if (it.start <= interval.end) {
                        overlaps.add(it)
                    }

                Direction.RIGHT ->
                    if (it.end >= interval.start) {
                        overlaps.add(it)
                    }
            }
        }
        return overlaps
    }

    private fun findOverlappingRanges(node: IntervalNode?, interval: Intervalable): List<Intervalable> =
        node?.findOverlaps(interval) ?: LinkedList()

    override fun equalProperties(other: Any): Boolean {
        return other is IntervalNode &&
                left == other.left &&
                right == other.right &&
                median == other.median &&
                intervals == other.intervals
    }
}
