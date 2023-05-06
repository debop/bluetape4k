package io.bluetape4k.utils.times.range

import io.bluetape4k.support.areEquals
import io.bluetape4k.support.hashOf
import io.bluetape4k.utils.times.isNegative
import io.bluetape4k.utils.times.isPositive
import java.time.temporal.Temporal
import java.time.temporal.TemporalAmount

/**
 * [Temporal] 의 범위를 나타내지만, 마지막 요소를 제외한 Open 된 Range를 표현합니다. `( min <= x < max )` 와 같습니다.
 *
 * @param start
 * @param endInclusive
 * @param step
 */
open class TemporalOpenedProgression<T> protected constructor(
    start: T,
    endInclusive: T,
    step: TemporalAmount,
): TemporalClosedProgression<T>(start, endInclusive, step) where T: Temporal, T: Comparable<T> {

    override fun equals(other: Any?): Boolean = when (other) {
        is TemporalOpenedProgression<*> ->
            (isEmpty() && other.isEmpty()) ||
                (areEquals(first, other.first) &&
                    areEquals(last, other.last) &&
                    areEquals(step, other.step))

        else -> false
    }

    override fun hashCode(): Int = hashOf(first, last, step)

    @Suppress("UNCHECKED_CAST")
    override fun sequence(): Sequence<T> = sequence seq@{
        fun canContinue(current: T): Boolean = when {
            step.isPositive -> current < last
            step.isNegative -> current > last
            else            -> false
        }

        var current = first

        while (canContinue(current)) {
            yield(current)
            current = current.plus(step) as T
        }
    }
}
