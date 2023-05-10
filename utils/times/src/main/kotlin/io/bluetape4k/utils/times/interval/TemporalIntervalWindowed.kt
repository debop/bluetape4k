package io.bluetape4k.utils.times.interval

import io.bluetape4k.core.assertPositiveNumber
import io.bluetape4k.utils.times.startOf
import io.bluetape4k.utils.times.temporalAmount
import java.time.temporal.ChronoUnit
import java.time.temporal.Temporal


private val SupportChronoUnits =
    arrayOf(
        ChronoUnit.YEARS,
        ChronoUnit.MONTHS,
        ChronoUnit.WEEKS,
        ChronoUnit.DAYS,
        ChronoUnit.HOURS,
        ChronoUnit.MINUTES,
        ChronoUnit.SECONDS,
        ChronoUnit.MILLIS
    )

fun <T> ReadableTemporalInterval<T>.chunked(
    size: Int,
    unit: ChronoUnit,
): Sequence<List<T>> where T: Temporal, T: Comparable<T> {
    return windowed(size, size, unit)
}

fun <T> ReadableTemporalInterval<T>.chunkYears(size: Int): Sequence<List<T>> where T: Temporal, T: Comparable<T> {
    return chunked(size, ChronoUnit.YEARS)
}

fun <T> ReadableTemporalInterval<T>.chunkMonths(size: Int): Sequence<List<T>> where T: Temporal, T: Comparable<T> {
    return chunked(size, ChronoUnit.MONTHS)
}

fun <T> ReadableTemporalInterval<T>.chunkWeeks(size: Int): Sequence<List<T>> where T: Temporal, T: Comparable<T> {
    return chunked(size, ChronoUnit.WEEKS)
}


fun <T> ReadableTemporalInterval<T>.chunkDays(size: Int): Sequence<List<T>> where T: Temporal, T: Comparable<T> {
    return chunked(size, ChronoUnit.DAYS)
}

fun <T> ReadableTemporalInterval<T>.chunkHours(size: Int): Sequence<List<T>> where T: Temporal, T: Comparable<T> {
    return chunked(size, ChronoUnit.HOURS)
}

fun <T> ReadableTemporalInterval<T>.chunkMinutes(size: Int): Sequence<List<T>> where T: Temporal, T: Comparable<T> {
    return chunked(size, ChronoUnit.MINUTES)
}

fun <T> ReadableTemporalInterval<T>.chunkSeconds(size: Int): Sequence<List<T>> where T: Temporal, T: Comparable<T> {
    return chunked(size, ChronoUnit.SECONDS)
}

fun <T> ReadableTemporalInterval<T>.chunkMillis(size: Int): Sequence<List<T>> where T: Temporal, T: Comparable<T> {
    return chunked(size, ChronoUnit.MILLIS)
}

//
// windowed
//

@Suppress("UNCHECKED_CAST")
fun <T> ReadableTemporalInterval<T>.windowed(
    size: Int,
    step: Int = 1,
    unit: ChronoUnit = ChronoUnit.YEARS,
): Sequence<List<T>>
    where T: Temporal, T: Comparable<T> {
    size.assertPositiveNumber("size")
    step.assertPositiveNumber("step")
    assert(unit in SupportChronoUnits) { "Not supported ChronoUnit. unit=$unit" }

    return sequence {
        var current: T = startInclusive.startOf(unit)
        val increment = step.temporalAmount(unit)

        while (current < endExclusive) {
            yield(List(size) { (current + it.temporalAmount(unit)) as T }.takeWhile { it < endExclusive })
            current = (current + increment) as T
        }
    }
}

fun <T> ReadableTemporalInterval<T>.windowedYears(
    size: Int,
    step: Int = 1,
): Sequence<List<T>> where T: Temporal, T: Comparable<T> =
    windowed(size, step, ChronoUnit.YEARS)

fun <T> ReadableTemporalInterval<T>.windowedMonths(
    size: Int,
    step: Int = 1,
): Sequence<List<T>> where T: Temporal, T: Comparable<T> =
    windowed(size, step, ChronoUnit.MONTHS)

fun <T> ReadableTemporalInterval<T>.windowedWeeks(
    size: Int,
    step: Int = 1,
): Sequence<List<T>> where T: Temporal, T: Comparable<T> =
    windowed(size, step, ChronoUnit.WEEKS)

fun <T> ReadableTemporalInterval<T>.windowedDays(
    size: Int,
    step: Int = 1,
): Sequence<List<T>> where T: Temporal, T: Comparable<T> =
    windowed(size, step, ChronoUnit.DAYS)

fun <T> ReadableTemporalInterval<T>.windowedHours(
    size: Int,
    step: Int = 1,
): Sequence<List<T>> where T: Temporal, T: Comparable<T> =
    windowed(size, step, ChronoUnit.HOURS)

fun <T> ReadableTemporalInterval<T>.windowedMinutes(
    size: Int,
    step: Int = 1,
): Sequence<List<T>> where T: Temporal, T: Comparable<T> =
    windowed(size, step, ChronoUnit.MINUTES)

fun <T> ReadableTemporalInterval<T>.windowedSeconds(
    size: Int,
    step: Int = 1,
): Sequence<List<T>> where T: Temporal, T: Comparable<T> =
    windowed(size, step, ChronoUnit.SECONDS)

fun <T> ReadableTemporalInterval<T>.windowedMillis(
    size: Int,
    step: Int = 1,
): Sequence<List<T>> where T: Temporal, T: Comparable<T> =
    windowed(size, step, ChronoUnit.MILLIS)


//
// Zip with Next
//
@Suppress("UNCHECKED_CAST")
fun <T> ReadableTemporalInterval<T>.zipWithNext(unit: ChronoUnit): Sequence<Pair<T, T>> where T: Temporal, T: Comparable<T> {
    assert(unit in SupportChronoUnits) { "Not supported ChronoUnit. unit=$unit" }

    return sequence {
        var current = startInclusive.startOf(unit)
        val increment = 1.temporalAmount(unit)
        val limit: T = (endExclusive - increment) as T

        while (current < limit) {
            val second = (current + increment) as T
            yield(current to second)
            current = second
        }
    }
}

fun <T> ReadableTemporalInterval<T>.zipWithNextYear(): Sequence<Pair<T, T>> where T: Temporal, T: Comparable<T> =
    zipWithNext(ChronoUnit.YEARS)

fun <T> ReadableTemporalInterval<T>.zipWithNextMonth(): Sequence<Pair<T, T>> where T: Temporal, T: Comparable<T> =
    zipWithNext(ChronoUnit.MONTHS)

fun <T> ReadableTemporalInterval<T>.zipWithNextWeek(): Sequence<Pair<T, T>> where T: Temporal, T: Comparable<T> =
    zipWithNext(ChronoUnit.WEEKS)

fun <T> ReadableTemporalInterval<T>.zipWithNextDay(): Sequence<Pair<T, T>> where T: Temporal, T: Comparable<T> =
    zipWithNext(ChronoUnit.DAYS)

fun <T> ReadableTemporalInterval<T>.zipWithNextHour(): Sequence<Pair<T, T>> where T: Temporal, T: Comparable<T> =
    zipWithNext(ChronoUnit.HOURS)

fun <T> ReadableTemporalInterval<T>.zipWithNextMinute(): Sequence<Pair<T, T>> where T: Temporal, T: Comparable<T> =
    zipWithNext(ChronoUnit.MINUTES)

fun <T> ReadableTemporalInterval<T>.zipWithNextSecond(): Sequence<Pair<T, T>> where T: Temporal, T: Comparable<T> =
    zipWithNext(ChronoUnit.SECONDS)

fun <T> ReadableTemporalInterval<T>.zipWithNextMilli(): Sequence<Pair<T, T>> where T: Temporal, T: Comparable<T> =
    zipWithNext(ChronoUnit.MILLIS)
