package io.bluetape4k.utils.times.range.coroutines

import io.bluetape4k.core.assertPositiveNumber
import io.bluetape4k.utils.times.range.SupportChronoUnits
import io.bluetape4k.utils.times.range.TemporalClosedProgression
import io.bluetape4k.utils.times.range.TemporalClosedRange
import io.bluetape4k.utils.times.startOf
import io.bluetape4k.utils.times.temporalAmount
import java.time.temporal.ChronoUnit
import java.time.temporal.Temporal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


fun <T> TemporalClosedProgression<T>.asFlow(): Flow<T> where T: Temporal, T: Comparable<T> =
    flow {
        forEach { value -> emit(value) }
    }

fun <T> TemporalClosedRange<T>.asFlow(): Flow<T> where T: Temporal, T: Comparable<T> =
    flow {
        forEach { value -> emit(value) }
    }

@Suppress("UNCHECKED_CAST")
@JvmOverloads
fun <T> TemporalClosedRange<T>.windowedFlow(
    size: Int,
    step: Int = 1,
    unit: ChronoUnit = ChronoUnit.YEARS,
): Flow<List<T>>
    where T: Temporal, T: Comparable<T> {
    size.assertPositiveNumber("size")
    step.assertPositiveNumber("step")
    assert(unit in SupportChronoUnits) { "Not supoorted ChronoUnit. unit=$unit" }

    return flow {
        var current: T = start.startOf(unit)
        val increment = step.temporalAmount(unit)

        while (current <= endInclusive) {
            val item = List(size) { (current + it.temporalAmount(unit)) as T }.takeWhile { it <= endInclusive }
            emit(item)
            current = (current + increment) as T
        }
    }
}

@JvmOverloads
fun <T> TemporalClosedRange<T>.windowedFlowYears(
    size: Int,
    step: Int = 1,
): Flow<List<T>> where T: Temporal, T: Comparable<T> =
    windowedFlow(size, step, ChronoUnit.YEARS)

@JvmOverloads
fun <T> TemporalClosedRange<T>.windowedFlowMonths(
    size: Int,
    step: Int = 1,
): Flow<List<T>> where T: Temporal, T: Comparable<T> =
    windowedFlow(size, step, ChronoUnit.MONTHS)

@JvmOverloads
fun <T> TemporalClosedRange<T>.windowedFlowWeeks(
    size: Int,
    step: Int = 1,
): Flow<List<T>> where T: Temporal, T: Comparable<T> =
    windowedFlow(size, step, ChronoUnit.WEEKS)

@JvmOverloads
fun <T> TemporalClosedRange<T>.windowedFlowDays(
    size: Int,
    step: Int = 1,
): Flow<List<T>> where T: Temporal, T: Comparable<T> =
    windowedFlow(size, step, ChronoUnit.DAYS)

@JvmOverloads
fun <T> TemporalClosedRange<T>.windowedFlowHours(
    size: Int,
    step: Int = 1,
): Flow<List<T>> where T: Temporal, T: Comparable<T> =
    windowedFlow(size, step, ChronoUnit.HOURS)

@JvmOverloads
fun <T> TemporalClosedRange<T>.windowedFlowMinutes(
    size: Int,
    step: Int = 1,
): Flow<List<T>> where T: Temporal, T: Comparable<T> =
    windowedFlow(size, step, ChronoUnit.MINUTES)

@JvmOverloads
fun <T> TemporalClosedRange<T>.windowedFlowSeconds(
    size: Int,
    step: Int = 1,
): Flow<List<T>> where T: Temporal, T: Comparable<T> =
    windowedFlow(size, step, ChronoUnit.SECONDS)

@JvmOverloads
fun <T> TemporalClosedRange<T>.windowedFlowMillis(
    size: Int,
    step: Int = 1,
): Flow<List<T>> where T: Temporal, T: Comparable<T> =
    windowedFlow(size, step, ChronoUnit.MILLIS)


@JvmOverloads
fun <T> TemporalClosedRange<T>.chunkedFlow(
    size: Int,
    unit: ChronoUnit = ChronoUnit.YEARS,
): Flow<List<T>> where T: Temporal, T: Comparable<T> =
    windowedFlow(size, size, unit)

fun <T> TemporalClosedRange<T>.chunkedFlowYears(size: Int): Flow<List<T>> where T: Temporal, T: Comparable<T> =
    chunkedFlow(size, ChronoUnit.YEARS)


fun <T> TemporalClosedRange<T>.chunkedFlowMonths(size: Int): Flow<List<T>> where T: Temporal, T: Comparable<T> =
    chunkedFlow(size, ChronoUnit.MONTHS)


fun <T> TemporalClosedRange<T>.chunkedFlowWeeks(size: Int): Flow<List<T>> where T: Temporal, T: Comparable<T> =
    chunkedFlow(size, ChronoUnit.WEEKS)


fun <T> TemporalClosedRange<T>.chunkedFlowDays(size: Int): Flow<List<T>> where T: Temporal, T: Comparable<T> =
    chunkedFlow(size, ChronoUnit.DAYS)


fun <T> TemporalClosedRange<T>.chunkedFlowHours(size: Int): Flow<List<T>> where T: Temporal, T: Comparable<T> =
    chunkedFlow(size, ChronoUnit.HOURS)


fun <T> TemporalClosedRange<T>.chunkedFlowMinutes(size: Int): Flow<List<T>> where T: Temporal, T: Comparable<T> =
    chunkedFlow(size, ChronoUnit.MINUTES)


fun <T> TemporalClosedRange<T>.chunkedFlowSeconds(size: Int): Flow<List<T>> where T: Temporal, T: Comparable<T> =
    chunkedFlow(size, ChronoUnit.SECONDS)


fun <T> TemporalClosedRange<T>.chunkedFlowMillis(size: Int): Flow<List<T>> where T: Temporal, T: Comparable<T> =
    chunkedFlow(size, ChronoUnit.MILLIS)


@Suppress("UNCHECKED_CAST")
fun <T> TemporalClosedRange<T>.zipWithNextFlow(unit: ChronoUnit): Flow<Pair<T, T>> where T: Temporal, T: Comparable<T> {
    assert(unit in SupportChronoUnits) { "Not supoorted ChronoUnit. unit=$unit" }

    return flow {
        var current: T = start.startOf(unit)
        val increment = 1.temporalAmount(unit)
        val limit: T = (endInclusive - increment) as T

        while (current <= limit) {
            val next = (current + increment) as T
            emit(current to next)
            current = next
        }
    }
}

fun <T> TemporalClosedRange<T>.zipWithNextFlowYears(): Flow<Pair<T, T>> where T: Temporal, T: Comparable<T> =
    zipWithNextFlow(ChronoUnit.YEARS)

fun <T> TemporalClosedRange<T>.zipWithNextFlowMonths(): Flow<Pair<T, T>> where T: Temporal, T: Comparable<T> =
    zipWithNextFlow(ChronoUnit.MONTHS)

fun <T> TemporalClosedRange<T>.zipWithNextFlowDays(): Flow<Pair<T, T>> where T: Temporal, T: Comparable<T> =
    zipWithNextFlow(ChronoUnit.DAYS)

fun <T> TemporalClosedRange<T>.zipWithNextFlowHours(): Flow<Pair<T, T>> where T: Temporal, T: Comparable<T> =
    zipWithNextFlow(ChronoUnit.HOURS)

fun <T> TemporalClosedRange<T>.zipWithNextFlowMinutes(): Flow<Pair<T, T>> where T: Temporal, T: Comparable<T> =
    zipWithNextFlow(ChronoUnit.MINUTES)

fun <T> TemporalClosedRange<T>.zipWithNextFlowSeconds(): Flow<Pair<T, T>> where T: Temporal, T: Comparable<T> =
    zipWithNextFlow(ChronoUnit.SECONDS)

fun <T> TemporalClosedRange<T>.zipWithNextFlowMillis(): Flow<Pair<T, T>> where T: Temporal, T: Comparable<T> =
    zipWithNextFlow(ChronoUnit.MILLIS)