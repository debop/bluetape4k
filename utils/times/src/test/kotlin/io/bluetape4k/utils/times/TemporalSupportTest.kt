package io.bluetape4k.utils.times

import io.bluetape4k.logging.KLogging
import java.time.DayOfWeek
import java.time.temporal.ChronoUnit
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TemporalSupportTest {

    companion object: KLogging()

    @Test
    fun `get startOfYear from Temporal`() {
        nowInstant().startOfYear().toZonedDateTime().dayOfYear shouldBeEqualTo 1
        nowLocalDate().startOfYear().dayOfYear shouldBeEqualTo 1
        nowLocalDateTime().startOfYear().dayOfYear shouldBeEqualTo 1
        nowOffsetDateTime().startOfYear().dayOfYear shouldBeEqualTo 1
        nowZonedDateTime().startOfYear().dayOfYear shouldBeEqualTo 1
    }

    @Test
    fun `get startOfMonth from Temporal`() {
        nowInstant().startOfMonth().toZonedDateTime().dayOfMonth shouldBeEqualTo 1
        nowLocalDate().startOfMonth().dayOfMonth shouldBeEqualTo 1
        nowLocalDateTime().startOfMonth().dayOfMonth shouldBeEqualTo 1
        nowOffsetDateTime().startOfMonth().dayOfMonth shouldBeEqualTo 1
        nowZonedDateTime().startOfMonth().dayOfMonth shouldBeEqualTo 1
    }

    @Test
    fun `get startOfDay from Temporal`() {

        // NOTE : Time zone에 따른 시간을 고려해야 합니다
        nowInstant().startOfDay().toZonedDateTime(UtcZoneId).hour shouldBeEqualTo 0

        nowLocalDateTime().startOfDay().hour shouldBeEqualTo 0
        nowOffsetDateTime().startOfDay().hour shouldBeEqualTo 0
        nowZonedDateTime().startOfDay().hour shouldBeEqualTo 0
    }

    @Test
    fun `get startOfHour from Temporal`() {

        nowInstant().startOfHour().toZonedDateTime(UtcZoneId).minute shouldBeEqualTo 0

        nowLocalDateTime().startOfHour().minute shouldBeEqualTo 0
        nowOffsetDateTime().startOfHour().minute shouldBeEqualTo 0
        nowZonedDateTime().startOfHour().minute shouldBeEqualTo 0

        nowLocalTime().startOfHour().minute shouldBeEqualTo 0
        nowOffsetTime().startOfHour().minute shouldBeEqualTo 0
    }

    @Test
    fun `get startOfMinute from Temporal`() {

        nowInstant().startOfMinute().toZonedDateTime(UtcZoneId).second shouldBeEqualTo 0

        nowLocalDateTime().startOfMinute().second shouldBeEqualTo 0
        nowOffsetDateTime().startOfMinute().second shouldBeEqualTo 0
        nowZonedDateTime().startOfMinute().second shouldBeEqualTo 0

        nowLocalTime().startOfMinute().second shouldBeEqualTo 0
        nowOffsetTime().startOfMinute().second shouldBeEqualTo 0
    }

    @Test
    fun `get startOfSecond from Temporal`() {

        nowInstant().startOfSecond().toZonedDateTime(UtcZoneId).nano shouldBeEqualTo 0

        nowLocalDateTime().startOfSecond().nano shouldBeEqualTo 0
        nowOffsetDateTime().startOfSecond().nano shouldBeEqualTo 0
        nowZonedDateTime().startOfSecond().nano shouldBeEqualTo 0

        nowLocalTime().startOfSecond().nano shouldBeEqualTo 0
        nowOffsetTime().startOfSecond().nano shouldBeEqualTo 0
    }

    @Test
    fun `temporal adjuster operators`() {
        val now = localDateTimeOf(2020, 10, 14, 6, 55, 44)

        now.firstOfMonth shouldBeEqualTo localDateTimeOf(2020, 10, 1, 6, 55, 44)
        now.lastOfMonth shouldBeEqualTo localDateTimeOf(2020, 10, 31, 6, 55, 44)
        now.firstOfNextMonth shouldBeEqualTo localDateTimeOf(2020, 11, 1, 6, 55, 44)

        now.firstOfYear shouldBeEqualTo localDateTimeOf(2020, 1, 1, 6, 55, 44)
        now.lastOfYear shouldBeEqualTo localDateTimeOf(2020, 12, 31, 6, 55, 44)
        now.firstOfNextYear shouldBeEqualTo localDateTimeOf(2021, 1, 1, 6, 55, 44)
    }

    @Test
    fun `temporal adjuster by with operator`() {
        val now = localDateTimeOf(2020, 10, 14, 6, 55, 44)

        now.dayOfWeekInMonth(1, DayOfWeek.MONDAY) shouldBeEqualTo localDateTimeOf(2020, 10, 5, 6, 55, 44)
        now.dayOfWeekInMonth(2, DayOfWeek.MONDAY) shouldBeEqualTo localDateTimeOf(2020, 10, 12, 6, 55, 44)
        now.dayOfWeekInMonth(5, DayOfWeek.MONDAY) shouldBeEqualTo localDateTimeOf(2020, 11, 2, 6, 55, 44)

        now.firstInMonth(DayOfWeek.MONDAY) shouldBeEqualTo localDateTimeOf(2020, 10, 5, 6, 55, 44)
        now.firstInMonth(DayOfWeek.TUESDAY) shouldBeEqualTo localDateTimeOf(2020, 10, 6, 6, 55, 44)
        now.firstInMonth(DayOfWeek.SUNDAY) shouldBeEqualTo localDateTimeOf(2020, 10, 4, 6, 55, 44)

        now.lastInMonth(DayOfWeek.MONDAY) shouldBeEqualTo localDateTimeOf(2020, 10, 26, 6, 55, 44)
        now.lastInMonth(DayOfWeek.FRIDAY) shouldBeEqualTo localDateTimeOf(2020, 10, 30, 6, 55, 44)
        now.lastInMonth(DayOfWeek.SUNDAY) shouldBeEqualTo localDateTimeOf(2020, 10, 25, 6, 55, 44)

        now.previous(DayOfWeek.MONDAY) shouldBeEqualTo localDateTimeOf(2020, 10, 12, 6, 55, 44)
        now.previousOrSame(DayOfWeek.MONDAY) shouldBeEqualTo localDateTimeOf(2020, 10, 12, 6, 55, 44)
        now.previousOrSame(DayOfWeek.WEDNESDAY) shouldBeEqualTo localDateTimeOf(2020, 10, 14, 6, 55, 44)

        now.next(DayOfWeek.MONDAY) shouldBeEqualTo localDateTimeOf(2020, 10, 19, 6, 55, 44)
        now.nextOrSame(DayOfWeek.MONDAY) shouldBeEqualTo localDateTimeOf(2020, 10, 19, 6, 55, 44)
        now.nextOrSame(DayOfWeek.WEDNESDAY) shouldBeEqualTo localDateTimeOf(2020, 10, 14, 6, 55, 44)
    }

    @Test
    fun `Temporal supports operator`() {
        assertFalse { nowInstant() supports ChronoUnit.ERAS }
        assertFalse { nowInstant() supports ChronoUnit.CENTURIES }
        assertFalse { nowInstant() supports ChronoUnit.YEARS }
        assertFalse { nowInstant() supports ChronoUnit.MONTHS }

        assertTrue { nowInstant() supports ChronoUnit.DAYS }
        assertTrue { nowInstant() supports ChronoUnit.HALF_DAYS }
        assertTrue { nowInstant() supports ChronoUnit.HOURS }

        assertTrue { nowZonedDateTime() supports ChronoUnit.ERAS }
        assertTrue { nowZonedDateTime() supports ChronoUnit.CENTURIES }
        assertTrue { nowZonedDateTime() supports ChronoUnit.YEARS }
        assertTrue { nowZonedDateTime() supports ChronoUnit.DAYS }
        assertTrue { nowZonedDateTime() supports ChronoUnit.HALF_DAYS }
        assertTrue { nowZonedDateTime() supports ChronoUnit.HOURS }

        assertTrue { nowLocalDate() supports ChronoUnit.ERAS }
        assertTrue { nowLocalDate() supports ChronoUnit.CENTURIES }
        assertTrue { nowLocalDate() supports ChronoUnit.YEARS }
        assertTrue { nowLocalDate() supports ChronoUnit.DAYS }
        assertFalse { nowLocalDate() supports ChronoUnit.HALF_DAYS }
        assertFalse { nowLocalDate() supports ChronoUnit.HOURS }

        assertTrue { nowLocalDateTime() supports ChronoUnit.ERAS }
        assertTrue { nowLocalDateTime() supports ChronoUnit.CENTURIES }
        assertTrue { nowLocalDateTime() supports ChronoUnit.YEARS }
        assertTrue { nowLocalDateTime() supports ChronoUnit.DAYS }
        assertTrue { nowLocalDateTime() supports ChronoUnit.HALF_DAYS }
        assertTrue { nowLocalDateTime() supports ChronoUnit.HOURS }
    }
}
