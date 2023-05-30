package io.bluetape4k.utils.times.interval

import io.bluetape4k.logging.KLogging
import io.bluetape4k.utils.times.TimeSpec.UtcZoneId
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.Temporal
import java.time.temporal.TemporalAmount

/**
 * joda-time의 Interval을 참고하여 구현한 클래스입니다.
 *
 * 참고: [Interval.java](https://gist.github.com/simon04/26f68a3f21f76dc0bc1ff012676432c9)
 */
@Suppress("UNCHECKED_CAST")
class TemporalInterval<T> private constructor(
    override val startInclusive: T,
    override val endExclusive: T,
    override val zoneId: ZoneId,
): AbstractTemporalInterval<T>() where T: Temporal, T: Comparable<T> {

    companion object: KLogging() {
        @JvmStatic
        operator fun <T> invoke(
            start: T,
            end: T,
            zoneId: ZoneId = UtcZoneId,
        ): TemporalInterval<T> where T: Temporal, T: Comparable<T> {
            check(start <= end) { "The end instant[$end] must be greater than the start instant[$start]." }
            return TemporalInterval(start, end, zoneId)
        }

        @JvmStatic
        operator fun <T> invoke(
            start: T,
            duration: TemporalAmount,
            zoneId: ZoneId = UtcZoneId,
        ): TemporalInterval<T> where T: Temporal, T: Comparable<T> {
            return invoke(start, (start + duration) as T, zoneId)
        }

        @JvmStatic
        operator fun <T> invoke(
            duration: TemporalAmount,
            end: T,
            zoneId:
            ZoneId = UtcZoneId,
        ): TemporalInterval<T> where T: Temporal, T: Comparable<T> {
            return invoke((end - duration) as T, end, zoneId)
        }

        /**
         * [ZonedDateTime]로 표현한 기간을 parsing 합니다.
         * @param str String
         * @return TemporalInterval
         */
        fun parse(str: String): ZonedDateTimeInterval {
            val (leftStr, rightStr) = str.split(ReadableTemporalInterval.SEPARATOR, limit = 2)

            val start = ZonedDateTime.parse(leftStr.trim())
            val end = ZonedDateTime.parse(rightStr.trim())

            return temporalIntervalOf(start, end)
        }

        /**
         * [OffsetDateTime] 로 표현한 기간을 parsing 합니다.
         * @param str CharSequence
         * @return TemporalInterval
         */
        fun parseWithOffset(str: CharSequence): ZonedDateTimeInterval {
            val (leftStr, rightStr) = str.split(ReadableTemporalInterval.SEPARATOR, limit = 2)

            val start = ZonedDateTime.parse(leftStr.trim(), DateTimeFormatter.ISO_OFFSET_DATE_TIME)
            val end = ZonedDateTime.parse(rightStr.trim(), DateTimeFormatter.ISO_OFFSET_DATE_TIME)

            return temporalIntervalOf(start, end)
        }

    }

    /**
     * `start`를 기준으로 `duration`을 가지는 [TemporalInterval]을 빌드한다
     * @param duration TemporalAmount
     * @return TemporalInterval
     */
    fun withAmountAfterStart(duration: TemporalAmount): TemporalInterval<T> {
        return temporalIntervalOf(startInclusive, duration, zoneId)
    }

    /**
     * End 를 기준으로 지정한 `duration`을 가지는 [TemporalInterval]을 빌드한다
     * @param duration TemporalAmount
     * @return TemporalInterval
     */
    fun withAmountBeforeEnd(duration: TemporalAmount): TemporalInterval<T> {
        return temporalIntervalOf(duration, endExclusive, zoneId)
    }
}
