package io.bluetape4k.collections.eclipse.primitives

import io.bluetape4k.logging.KLogging
import org.eclipse.collections.impl.list.Interval
import org.eclipse.collections.impl.list.mutable.primitive.IntArrayList
import org.eclipse.collections.impl.list.mutable.primitive.LongArrayList
import org.eclipse.collections.impl.list.primitive.IntInterval
import org.eclipse.collections.impl.list.primitive.LongInterval

object Intervals: KLogging() {

    object Ints: KLogging() {
        inline fun runEach(interval: Interval, crossinline action: (Int) -> Unit) {
            interval.forEach { action(it) }
        }

        fun range(from: Int, to: Int, step: Int = 1): IntArrayList {
            return intArrayListOf(IntInterval.fromToBy(from, to, step))
        }

        fun range(interval: IntInterval): IntArrayList {
            return intArrayListOf(interval)
        }

        fun widowed(interval: IntInterval, size: Int, step: Int = 1): List<IntArrayList> {
            return range(interval)
                .asList()
                .windowed(size, step)
                .map { intArrayListOf(it) }
        }
    }

    object Longs: KLogging() {

        inline fun runEach(interval: LongInterval, crossinline action: (Long) -> Unit) {
            interval.forEach { action(it) }
        }

        fun range(from: Long, to: Long, step: Long = 1L): LongArrayList {
            return longArrayListOf(LongInterval.fromToBy(from, to, step))
        }

        fun range(interval: LongInterval): LongArrayList {
            return longArrayListOf(interval)
        }

        fun windowed(interval: LongInterval, size: Int, step: Int = 1): List<LongArrayList> {
            return range(interval).asList()
                .windowed(size, step)
                .map { longArrayListOf(it) }
        }
    }
}
