package io.bluetape4k.utils.times.period.ranges

import io.bluetape4k.logging.KLogging
import io.bluetape4k.utils.times.HoursPerDay
import io.bluetape4k.utils.times.MonthsPerYear
import io.bluetape4k.utils.times.days
import io.bluetape4k.utils.times.endOfMonth
import io.bluetape4k.utils.times.nowZonedDateTime
import io.bluetape4k.utils.times.period.AbstractPeriodTest
import io.bluetape4k.utils.times.period.TimeCalendar
import io.bluetape4k.utils.times.startOfDay
import io.bluetape4k.utils.times.startOfMonth
import io.bluetape4k.utils.times.startOfYear
import io.bluetape4k.utils.times.todayZonedDateTime
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test


class DayRangeTest: AbstractPeriodTest() {

    companion object: KLogging()

    @Test
    fun `construct with now`() {
        val now = nowZonedDateTime()
        val startOfDay = now.startOfDay()

        val dayRange = DayRange(now, TimeCalendar.EmptyOffset)

        dayRange.start shouldBeEqualTo startOfDay
        dayRange.end shouldBeEqualTo startOfDay.plusDays(1)
    }

    @Test
    fun `default constructor`() {
        val startOfYear = nowZonedDateTime().startOfYear()

        (1..MonthsPerYear).toList().parallelStream().forEach { m ->
            val startOfMonth = startOfMonth(startOfYear.year, m)
            val endOfMonth = endOfMonth(startOfYear.year, m)

            (startOfMonth.dayOfMonth until endOfMonth.dayOfMonth).forEach { day ->
                val dayRange = DayRange(startOfMonth + (day - startOfMonth.dayOfMonth).days())

                dayRange.year shouldBeEqualTo startOfYear.year
                dayRange.monthOfYear shouldBeEqualTo startOfMonth.monthValue
            }
        }
    }

    @Test
    fun `construct with moment`() {
        val now = nowZonedDateTime()

        val dayRange = DayRange(now)
        dayRange.start shouldBeEqualTo now.startOfDay()
    }

    @Test
    fun `dayOfWeek property`() {
        val now = nowZonedDateTime()
        val dayRange = DayRange(now, TimeCalendar.Default)

        dayRange.dayOfWeek shouldBeEqualTo now.dayOfWeek
    }

    @Test
    fun `add days`() {
        val now = nowZonedDateTime()
        val today = todayZonedDateTime()
        val dayRange = DayRange(now)

        dayRange.prevDay().start shouldBeEqualTo today.minusDays(1)
        dayRange.nextDay().start shouldBeEqualTo today.plusDays(1)
        dayRange.addDays(0) shouldBeEqualTo dayRange

        (-60..120).forEach {
            dayRange.addDays(it).start shouldBeEqualTo today.plusDays(it.toLong())
        }
    }

    @Test
    fun `get hour sequence`() {
        val dr = DayRange()
        val hours = dr.hourSequence()

        hours.count() shouldBeEqualTo HoursPerDay

        hours.forEachIndexed { index, hr ->
            hr.start shouldBeEqualTo dr.start.plusHours(index.toLong())
            hr.end shouldBeEqualTo hr.calendar.mapEnd(hr.start.plusHours(1))
        }
    }
}
