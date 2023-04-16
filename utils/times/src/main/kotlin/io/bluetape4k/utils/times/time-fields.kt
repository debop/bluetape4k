package io.bluetape4k.utils.times

import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.time.Year
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.TimeZone


const val NANO_PER_MILLIS: Long = 1_000_000L
const val NANO_PER_SECOND: Long = 1_000_000_000L

@JvmField
val MILLIS_IN_DAY: Long = Duration.ofDays(1).toMillis()

@JvmField
val MILLIS_IN_HOUR = Duration.ofHours(1).toMillis()

@JvmField
val MILLIS_IN_MINUTE = Duration.ofMinutes(1).toMillis()

@JvmField
val NANOS_IN_DAY: Long = Duration.ofDays(1).toNanos()

@JvmField
val NANOS_IN_HOUR: Long = Duration.ofHours(1).toNanos()

@JvmField
val NANOS_IN_MINUTER = Duration.ofMinutes(1).toNanos()

@JvmField
val NANOS_IN_SECOND = Duration.ofSeconds(1).toNanos()

/**
 * 기본 날짜 포맷 (ISO 기준)  예: '2011-12-03T10:15:30Z'
 */
@JvmField
val DefaultDateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ISO_INSTANT

@JvmField
val UtcZoneId: ZoneId = ZoneOffset.UTC

@JvmField
val UtcTimeZone: TimeZone = TimeZone.getTimeZone(UtcZoneId)

@JvmField
val UtcOffset: ZoneOffset = ZoneOffset.UTC

@JvmField
val SystemTimeZone: TimeZone = TimeZone.getDefault()

@JvmField
val SystemZoneId: ZoneId = ZoneId.systemDefault()

@JvmField
val SystemOffset: ZoneOffset = ZoneOffset.ofTotalSeconds(SystemTimeZone.rawOffset / 1000)


const val MonthsPerYear = 12
const val HalfyearsPerYear = 2
const val QuartersPerYear = 4
const val QuartersPerHalfyear = 2
const val MonthsPerHalfyear = 6
const val MonthsPerQuarter = 3
const val MaxWeeksPerYear = 54
const val MaxDaysPerMonth = 31
const val DaysPerWeek = 7
const val HoursPerDay = 24
const val MinutesPerHour = 60
const val SecondsPerMinute = 60

const val MillisPerSecond = 1000L
const val MillisPerMinute: Long = MillisPerSecond * SecondsPerMinute
const val MillisPerHour: Long = MillisPerMinute * MinutesPerHour
const val MillisPerDay: Long = MillisPerHour * HoursPerDay

const val MicrosPerMillis = 1000L
const val MicrosPerSecond = MicrosPerMillis * MillisPerSecond
const val MicrosPerMinute: Long = MicrosPerSecond * SecondsPerMinute
const val MicrosPerHour: Long = MicrosPerMinute * MinutesPerHour
const val MicrosPerDay: Long = MicrosPerHour * HoursPerDay

const val NanosPerMillis: Long = MicrosPerSecond
const val NanosPerSecond = NanosPerMillis * MillisPerSecond
const val NanosPerMinute: Long = NanosPerSecond * SecondsPerMinute
const val NanosPerHour: Long = NanosPerMinute * MinutesPerHour
const val NanosPerDay: Long = NanosPerHour * HoursPerDay

const val TicksPerMillisecond = 10000L
const val TicksPerSecond = TicksPerMillisecond * MillisPerSecond
const val TicksPerMinute = TicksPerSecond * SecondsPerMinute
const val TicksPerHour = TicksPerMinute * MinutesPerHour
const val TicksPerDay = TicksPerHour * HoursPerDay


@JvmField
val Weekdays = arrayOf(
    DayOfWeek.MONDAY,
    DayOfWeek.TUESDAY,
    DayOfWeek.WEDNESDAY,
    DayOfWeek.THURSDAY,
    DayOfWeek.FRIDAY
)

@JvmField
val Weekends = arrayOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)

@JvmField
val FirstDayOfWeek: DayOfWeek = DayOfWeek.MONDAY

@JvmField
val FirstHalfyearMonths = intArrayOf(1, 2, 3, 4, 5, 6)

@JvmField
val SecondHalfyearMonths = intArrayOf(7, 8, 9, 10, 11, 12)


@JvmField
val Q1Months = intArrayOf(1, 2, 3)

@JvmField
val Q2Months = intArrayOf(4, 5, 6)

@JvmField
val Q3Months = intArrayOf(7, 8, 9)

@JvmField
val Q4Months = intArrayOf(10, 11, 12)

@JvmField
val EmptyDuration: Duration = Duration.ZERO

@JvmField
val MinDuration: Duration = 0.nanos()

@JvmField
val MaxDuration: Duration = Long.MAX_VALUE.seconds()

@JvmField
val MinPositiveDuration: Duration = 1.nanos()

@JvmField
val MinNegativeDuration: Duration = (-1).nanos()

@JvmField
val MinPeriodTime: ZonedDateTime = zonedDateTimeOf(LocalDate.MIN, LocalTime.MIDNIGHT)

@JvmField
val MaxPeriodTime: ZonedDateTime = zonedDateTimeOf(Year.MAX_VALUE, 12, 31)

@JvmField
val DefaultStartOffset: Duration = EmptyDuration

@JvmField
val DefaultEndOffset: Duration = MinNegativeDuration

fun DayOfWeek.isWeekend(): Boolean = Weekends.contains(this)
