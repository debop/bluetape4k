package io.bluetape4k.utils.times.period

import io.bluetape4k.core.SortDirection
import java.time.ZonedDateTime

interface ITimePeriodContainer: MutableList<ITimePeriod>, ITimePeriod {

    val periods: MutableList<ITimePeriod>

    override var start: ZonedDateTime
    override val readonly: Boolean

    override fun addAll(elements: Collection<ITimePeriod>): Boolean

    fun addAll(array: Array<out ITimePeriod>): Boolean = addAll(array.asList())

    fun containsPeriod(target: ITimePeriod): Boolean = periods.contains(target)

    fun sortByStart(sortDir: SortDirection = SortDirection.ASC) {
        when (sortDir) {
            SortDirection.ASC  -> periods.sortBy { it.start }
            SortDirection.DESC -> periods.sortByDescending { it.start }
        }
    }

    fun sortByEnd(sortDir: SortDirection = SortDirection.ASC) {
        when (sortDir) {
            SortDirection.ASC  -> periods.sortBy { it.end }
            SortDirection.DESC -> periods.sortByDescending { it.end }
        }
    }

    fun sortByDuration(sortDir: SortDirection = SortDirection.ASC) {
        when (sortDir) {
            SortDirection.ASC  -> periods.sortBy { it.duration }
            SortDirection.DESC -> periods.sortByDescending { it.duration }
        }
    }

    fun compare(p1: ITimePeriod, p2: ITimePeriod): Int =
        p1.start.compareTo(p2.start)

}
