package io.bluetape4k.times

import io.bluetape4k.times.TimeSpec.DaysPerWeek
import io.bluetape4k.times.TimeSpec.MonthsPerQuarter
import io.bluetape4k.times.TimeSpec.NanosPerMillis
import io.bluetape4k.times.TimeSpec.SystemZoneId
import java.time.Clock
import java.time.DayOfWeek
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.Month
import java.time.MonthDay
import java.time.OffsetDateTime
import java.time.OffsetTime
import java.time.Period
import java.time.Year
import java.time.YearMonth
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

@JvmOverloads
fun nowInstant(zoneId: ZoneId = SystemZoneId): Instant = Instant.now(Clock.system(zoneId))

@JvmOverloads
fun nowLocalTime(zoneId: ZoneId = SystemZoneId): LocalTime = LocalTime.now(zoneId)

@JvmOverloads
fun nowLocalDate(zoneId: ZoneId = SystemZoneId): LocalDate = LocalDate.now(zoneId)

@JvmOverloads
fun nowLocalDateTime(zoneId: ZoneId = SystemZoneId): LocalDateTime = LocalDateTime.now(zoneId)

@JvmOverloads
fun nowOffsetDateTime(zoneId: ZoneId = SystemZoneId): OffsetDateTime = OffsetDateTime.now(zoneId)

@JvmOverloads
fun nowOffsetTime(zoneId: ZoneId = SystemZoneId): OffsetTime = OffsetTime.now(zoneId)

@JvmOverloads
fun nowZonedDateTime(zoneId: ZoneId = SystemZoneId): ZonedDateTime = ZonedDateTime.now(zoneId)

@JvmOverloads
fun todayInstant(zonedId: ZoneId = SystemZoneId): Instant = nowInstant(zonedId).truncatedTo(ChronoUnit.DAYS)

@JvmOverloads
fun todayLocalDateTime(zoneId: ZoneId = SystemZoneId): LocalDateTime =
    nowLocalDateTime(zoneId).truncatedTo(ChronoUnit.DAYS)

@JvmOverloads
fun todayOffsetDateTime(zoneId: ZoneId = SystemZoneId): OffsetDateTime = nowOffsetDateTime(zoneId).truncatedTo(
    ChronoUnit.DAYS
)

@JvmOverloads
fun todayZonedDateTime(zoneId: ZoneId = SystemZoneId): ZonedDateTime =
    nowZonedDateTime(zoneId).truncatedTo(ChronoUnit.DAYS)

fun Int.nanos(): Duration = Duration.ofNanos(this.toLong())
fun Int.micros(): Duration = Duration.ofNanos(this.toLong() * 1000L)
fun Int.millis(): Duration = Duration.ofMillis(this.toLong())
fun Int.seconds(): Duration = Duration.ofSeconds(this.toLong())
fun Int.minutes(): Duration = Duration.ofMinutes(this.toLong())
fun Int.hours(): Duration = Duration.ofHours(this.toLong())
fun Int.days(): Duration = Duration.ofDays(this.toLong())
fun Int.weeks(): Duration = (this * DaysPerWeek).days()

fun Int.dayPeriod(): Period = Period.ofDays(this)
fun Int.weekPeriod(): Period = Period.ofWeeks(this)
fun Int.monthPeriod(): Period = Period.ofMonths(this)
fun Int.quarterPeriod(): Period = Period.ofMonths(this * MonthsPerQuarter)
fun Int.yearPeriod(): Period = Period.ofYears(this)

fun Int.millisToNanos(): Int = (this * 1e6).toInt()
fun Int.isLeapYear(): Boolean = Year.of(this).isLeap

operator fun Int.times(duration: Duration): Duration = duration.multipliedBy(this.toLong())
operator fun Int.times(period: Period): Period = period.multipliedBy(this)
operator fun Duration.times(scalar: Int): Duration = multipliedBy(scalar.toLong())
operator fun Period.times(scalar: Int): Period = multipliedBy(scalar)

operator fun Duration.div(scalar: Int): Duration = dividedBy(scalar.toLong())
operator fun Period.div(scalar: Int): Period = multipliedBy(scalar)

fun Int.toInstant(): Instant = Instant.ofEpochMilli(this.toLong())


fun Long.nanos(): Duration = Duration.ofNanos(this)
fun Long.micros(): Duration = Duration.ofNanos(this * 1000L)
fun Long.millis(): Duration = Duration.ofMillis(this)
fun Long.seconds(): Duration = Duration.ofSeconds(this)
fun Long.minutes(): Duration = Duration.ofMinutes(this)
fun Long.hours(): Duration = Duration.ofHours(this)
fun Long.days(): Duration = Duration.ofDays(this)
fun Long.weeks(): Duration = (this * DaysPerWeek).days()

fun Long.dayPeriod(): Period = Period.ofDays(this.toInt())
fun Long.weekPeriod(): Period = Period.ofWeeks(this.toInt())
fun Long.monthPeriod(): Period = Period.ofMonths(this.toInt())
fun Long.quarterPeriod(): Period = Period.ofMonths(this.toInt() * MonthsPerQuarter)
fun Long.yearPeriod(): Period = Period.ofYears(this.toInt())

fun Long.millisToNanos(): Int = (this * NanosPerMillis).toInt()

fun Long.isLeapYear(): Boolean = Year.of(this.toInt()).isLeap

operator fun Long.times(duration: Duration): Duration = duration.multipliedBy(this)
operator fun Long.times(period: Period): Period = period.multipliedBy(this.toInt())
operator fun Duration.times(scalar: Long): Duration = multipliedBy(scalar)
operator fun Period.times(scalar: Long): Period = multipliedBy(scalar.toInt())

operator fun Duration.div(scalar: Long): Duration = dividedBy(scalar)
operator fun Period.div(scalar: Long): Period = multipliedBy(scalar.toInt())

fun Long.toInstant(): Instant = Instant.ofEpochMilli(this)

fun yearMonthOf(year: Int, month: Month): YearMonth = YearMonth.of(year, month)
fun yearMonthOf(year: Int, monthOfYear: Int): YearMonth = YearMonth.of(year, monthOfYear)

fun monthDayOf(monthOfYear: Int, dayOfMonth: Int): MonthDay = MonthDay.of(monthOfYear, dayOfMonth)
fun monthDayOf(month: Month, dayOfMonth: Int): MonthDay = MonthDay.of(month, dayOfMonth)

val NowLocalDate: LocalDate get() = LocalDate.now()
val NowLocalTime: LocalTime get() = LocalTime.now()
val NowLocalDateTime: LocalDateTime get() = LocalDateTime.now()

operator fun Duration.component1(): Long = this.seconds
operator fun Duration.component2(): Int = this.nano

operator fun Period.component1(): Int = this.years
operator fun Period.component2(): Int = this.months
operator fun Period.component3(): Int = this.days

operator fun Year.plus(month: Month): YearMonth = atMonth(month)
operator fun Year.plus(monthDay: MonthDay): LocalDate = atMonthDay(monthDay)
operator fun Year.plus(years: Int): Year = plusYears(years.toLong())
operator fun Year.minus(years: Int): Year = minusYears(years.toLong())

operator fun Month.plus(months: Int): Month = plus(months.toLong())
operator fun Month.minus(months: Int): Month = minus(months.toLong())

operator fun YearMonth.plus(year: Year): YearMonth = plusYears(year.value.toLong())
operator fun YearMonth.plus(month: Month): YearMonth = plusMonths(month.value.toLong())

operator fun Year.inc(): Year = plusYears(1L)
operator fun Year.dec(): Year = minusYears(1L)

operator fun Month.inc(): Month = plus(1L)
operator fun Month.dec(): Month = minus(1L)

operator fun DayOfWeek.inc(): DayOfWeek = plus(1L)
operator fun DayOfWeek.dec(): DayOfWeek = minus(1L)
