package io.bluetape4k.ahocorasick.interval

import io.bluetape4k.core.ValueObject
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.trace
import java.util.*

class IntervalTree private constructor(private val rootNode: IntervalNode) : ValueObject {

    constructor(intervals: List<Intervalable>) : this(IntervalNode(intervals))

    companion object : KLogging() {
        @JvmStatic
        operator fun invoke(rootNode: IntervalNode): IntervalTree {
            return IntervalTree(rootNode)
        }

        @JvmStatic
        operator fun invoke(intervals: List<Intervalable>): IntervalTree {
            return invoke(IntervalNode(intervals))
        }

        val reverseSizeComparator: Comparator<Intervalable> = IntervalableComparators.ReverseSizeComparator
        val positionComparator: Comparator<Intervalable> = IntervalableComparators.PositionComparator
    }

    fun findOverlaps(interval: Intervalable): MutableList<Intervalable> =
        rootNode.findOverlaps(interval)

    fun <T : Intervalable> removeOverlaps(intervals: Iterable<T>): MutableList<T> {
        // size가 큰 것부터
        val results = LinkedList<T>().apply { addAll(intervals) }
        results.sortWith(reverseSizeComparator)

        val removed = LinkedHashSet<Intervalable>()

        // 꼭 Sequence 방식으로 수행해야 updated된 removed를 사용할 수 있습니다.
        results
            .asSequence()
            .filterNot { removed.contains(it) }
            .forEach { target ->
                val overlaps = findOverlaps(target)
                log.trace { "target=$target, overlaps=$overlaps" }
                removed.addAll(overlaps)
            }

        // overlap 된 interval들을 삭제합니다.
        log.trace { "overlap 된 interval들을 삭제=$removed" }
        results.removeAll(removed)

        // sort the intervals, now on left-most position only
        results.sortWith(positionComparator)
        return results
    }

}
