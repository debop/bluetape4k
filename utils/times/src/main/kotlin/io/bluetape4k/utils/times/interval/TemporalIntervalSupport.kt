package io.bluetape4k.utils.times.interval

import io.bluetape4k.core.assertPositiveNumber
import io.bluetape4k.utils.times.TimeSpec.UtcZoneId
import io.bluetape4k.utils.times.startOf
import io.bluetape4k.utils.times.temporalAmount
import io.bluetape4k.utils.times.toEpochDay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.Duration
import java.time.LocalDate
import java.time.Period
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.time.temporal.Temporal
import java.time.temporal.TemporalAmount


//
// TemporalInterval
//

fun <T> temporalIntervalOf(
    start: T,
    end: T,
    zoneId: ZoneId = UtcZoneId,
): TemporalInterval<T> where T : Temporal, T : Comparable<T> {
    return when {
        start < end -> TemporalInterval(start, end, zoneId)
        else        -> TemporalInterval(end, start, zoneId)
    }
}

@Suppress("UNCHECKED_CAST")
fun <T> temporalIntervalOf(
    start: T,
    duration: TemporalAmount,
    zoneId: ZoneId = UtcZoneId,
): TemporalInterval<T> where T : Temporal, T : Comparable<T> =
    temporalIntervalOf(start, (start + duration) as T, zoneId)

@Suppress("UNCHECKED_CAST")
fun <T> temporalIntervalOf(
    duration: TemporalAmount,
    end: T,
    zoneId: ZoneId = UtcZoneId,
): TemporalInterval<T> where T : Temporal, T : Comparable<T> =
    temporalIntervalOf((end - duration) as T, end, zoneId)


//
// MutableTemporalInterval
//

/**
 * [MutableTemporalInterval] 인스턴스를 빌드합니다.
 *
 * @param T [Temporal]의 하위 수형
 * @param start 시작 시각
 * @param end   완료 시각
 * @param zoneId [ZoneId] (기본 값은 UTC)
 * @return [MutableTemporalInterval] 인스턴스
 */
fun <T> mutableTemporalIntervalOf(
    start: T,
    end: T,
    zoneId: ZoneId = UtcZoneId,
): MutableTemporalInterval<T> where T : Temporal, T : Comparable<T> {
    return when {
        start < end -> MutableTemporalInterval(start, end, zoneId)
        else        -> MutableTemporalInterval(end, start, zoneId)
    }
}

/**
 * [MutableTemporalInterval] 인스턴스를 빌드합니다.
 *
 * @param T [Temporal]의 하위 수형
 * @param start 시작 시각
 * @param duration 기간을 나타내는 정보
 * @param zoneId [ZoneId] (기본 값은 UTC)
 * @return [MutableTemporalInterval] 인스턴스
 */
@Suppress("UNCHECKED_CAST")
fun <T> mutableTemporalIntervalOf(
    start: T,
    duration: TemporalAmount,
    zoneId: ZoneId = UtcZoneId,
): MutableTemporalInterval<T> where T : Temporal, T : Comparable<T> =
    mutableTemporalIntervalOf(start, (start + duration) as T, zoneId)

/**
 * [MutableTemporalInterval] 인스턴스를 빌드합니다.
 *
 * @param T [Temporal]의 하위 수형
 * @param duration 기간을 나타내는 정보
 * @param end      완료 시각
 * @param zoneId [ZoneId] (기본 값은 UTC)
 * @return [MutableTemporalInterval] 인스턴스
 */
@Suppress("UNCHECKED_CAST")
fun <T> mutableTemporalIntervalOf(
    duration: TemporalAmount,
    end: T,
    zoneId: ZoneId = UtcZoneId,
): MutableTemporalInterval<T> where T : Temporal, T : Comparable<T> =
    mutableTemporalIntervalOf((end - duration) as T, end, zoneId)

//
// Conversions
//

/**
 * [ReadableTemporalInterval]의 시작시각과 완료시각으로 [Duration]을 빌드합니다.
 */
fun <T> ReadableTemporalInterval<T>.toDuration(): Duration where T : Temporal, T : Comparable<T> =
    Duration.between(startInclusive, endExclusive)

/**
 * [ReadableTemporalInterval]의 시작시각과 완료시각으로 Milliseconds 로 반환합니다.
 */
fun <T> ReadableTemporalInterval<T>.toDurationMillis(): Long where T : Temporal, T : Comparable<T> =
    toDuration().toMillis()

fun <T> ReadableTemporalInterval<T>.toInterval(): ReadableTemporalInterval<T> where T : Temporal, T : Comparable<T> {
    return temporalIntervalOf(startInclusive, endExclusive, zoneId)
}

fun <T> ReadableTemporalInterval<T>.toMutableInterval(): MutableTemporalInterval<T> where T : Temporal, T : Comparable<T> {
    return mutableTemporalIntervalOf(startInclusive, endExclusive, zoneId)
}

/**
 * [ReadableTemporalInterval]의 시작시각과 완료시각으로 [Period]을 빌드합니다.
 */
fun <T> ReadableTemporalInterval<T>.toPeriod(): Period where T : Temporal, T : Comparable<T> =
    Period.between(LocalDate.ofEpochDay(startInclusive.toEpochDay()), LocalDate.ofEpochDay(endExclusive.toEpochDay()))

/**
 * [ReadableTemporalInterval]의 시작시각과 완료시각으로 [unit] 단위의 [Period]을 빌드합니다.
 */
fun <T> ReadableTemporalInterval<T>.toPeriod(unit: ChronoUnit): Period where T : Temporal, T : Comparable<T> {
    return when (unit) {
        ChronoUnit.DAYS   -> Period.ofDays(toPeriod().days)
        ChronoUnit.WEEKS  -> Period.ofWeeks(toPeriod().days / 7)
        ChronoUnit.MONTHS -> Period.ofDays(toPeriod().months)
        ChronoUnit.YEARS  -> Period.ofDays(toPeriod().years)
        else              -> toPeriod()
    }
}


//
// Sequence of Interval
//

@Suppress("UNCHECKED_CAST")
fun <T> ReadableTemporalInterval<T>.sequence(
    step: Int,
    unit: ChronoUnit,
): Sequence<T> where T : Temporal, T : Comparable<T> {
    step.assertPositiveNumber("step")

    return sequence {
        var current = startInclusive.startOf(unit)
        val increment = step.temporalAmount(unit)

        // TemporalInterval 은 OpenedRange ( [start, end) ) 입니다.
        while (current < endExclusive) {
            yield(current)
            current = (current + increment) as T
        }
    }
}

@Suppress("UNCHECKED_CAST")
fun <T> ReadableTemporalInterval<T>.flow(
    step: Int,
    unit: ChronoUnit,
): Flow<T> where T : Temporal, T : Comparable<T> {
    step.assertPositiveNumber("step")

    return flow {
        var current = startInclusive.startOf(unit)
        val increment = step.temporalAmount(unit)

        // TemporalInterval 은 OpenedRange ( [start, end) ) 입니다.
        while (current < endExclusive) {
            emit(current)
            current = (current + increment) as T
        }
    }
}

fun <T> ReadableTemporalInterval<T>.millis(step: Int = 1): Sequence<T> where T : Temporal, T : Comparable<T> {
    return sequence(step, ChronoUnit.MILLIS)
}

fun <T> ReadableTemporalInterval<T>.seconds(step: Int = 1): Sequence<T> where T : Temporal, T : Comparable<T> {
    return sequence(step, ChronoUnit.SECONDS)
}

/**
 * 기간을 Minute 단위로 열거합니다.
 */
fun <T> ReadableTemporalInterval<T>.minutes(step: Int = 1): Sequence<T> where T : Temporal, T : Comparable<T> {
    return sequence(step, ChronoUnit.MINUTES)
}

/**
 * 기간을 Hour 단위로 열거합니다.
 */
fun <T> ReadableTemporalInterval<T>.hours(step: Int = 1): Sequence<T> where T : Temporal, T : Comparable<T> {
    return sequence(step, ChronoUnit.HOURS)
}

/**
 * 기간을 Day 단위로 열거합니다.
 */
fun <T> ReadableTemporalInterval<T>.days(step: Int = 1): Sequence<T> where T : Temporal, T : Comparable<T> {
    return sequence(step, ChronoUnit.DAYS)
}

fun <T> ReadableTemporalInterval<T>.weeks(step: Int = 1): Sequence<T> where T : Temporal, T : Comparable<T> {
    return sequence(step, ChronoUnit.WEEKS)
}

fun <T> ReadableTemporalInterval<T>.months(months: Int = 1): Sequence<T> where T : Temporal, T : Comparable<T> {
    return sequence(months, ChronoUnit.MONTHS)
}

fun <T> ReadableTemporalInterval<T>.years(step: Int = 1): Sequence<T> where T : Temporal, T : Comparable<T> {
    return sequence(step, ChronoUnit.YEARS)
}
