package io.bluetape4k.utils.times.period.timelines

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.trace
import io.bluetape4k.utils.times.period.ITimePeriod
import io.bluetape4k.utils.times.period.ITimePeriodCollection
import io.bluetape4k.utils.times.period.ITimePeriodContainer
import io.bluetape4k.utils.times.period.ITimePeriodMapper
import io.bluetape4k.utils.times.period.TimePeriodCollection
import io.bluetape4k.utils.times.period.TimeRange
import io.bluetape4k.utils.times.period.intersectRange
import io.bluetape4k.utils.times.period.intersectWith
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNot
import java.time.ZonedDateTime

/**
 * [ITimeLine]의 기본 구현체입니다.
 * [ITimePeriod] 컬렉션을 가지며, 이를 통해 여러 기간에 대한 Union, Intersection, Gap 등을 구할 수 있도록 합니다.
 */
class TimeLine<T: ITimePeriod> private constructor(
    override val periods: ITimePeriodContainer,
    private val _limits: ITimePeriod? = null,
    override val mapper: ITimePeriodMapper? = null,
): ITimeLine {

    companion object: KLogging() {
        @JvmStatic
        operator fun <T: ITimePeriod> invoke(
            periods: ITimePeriodContainer,
            limits: ITimePeriod? = null,
            mapper: ITimePeriodMapper? = null,
        ): TimeLine<T> {
            return TimeLine(periods, limits, mapper)
        }
    }

    override val limits: ITimePeriod
        get() = _limits?.let { TimeRange(it) } ?: TimeRange(periods)

    override suspend fun combinePeriods(): ITimePeriodCollection {
        if (periods.isEmpty())
            return TimePeriodCollection.EMPTY

        val moments = timeLineMoments(periods)
        return if (moments.isEmpty()) TimePeriodCollection(TimeRange(periods))
        else TimeLines.combinePeriods(moments)
    }

    override suspend fun intersectPeriods(): ITimePeriodCollection {
        if (periods.isEmpty()) {
            return TimePeriodCollection.EMPTY
        }
        val moments = timeLineMoments(periods)
        return if (moments.isEmpty()) TimePeriodCollection(TimeRange(periods))
        else TimeLines.intersectPeriods(moments)
    }

    override suspend fun calculateGaps(): ITimePeriodCollection {
        log.trace { "calculate gaps ... periods=$periods, limits=$limits" }

        val tpc = TimePeriodCollection()

        periods.periods.asFlow().buffer()
            .filter { tp -> limits.intersectWith(tp) }
            .collect { tp -> tpc.add(TimeRange(tp)) }

        val moments = timeLineMoments(periods)
        return if (moments.isEmpty()) {
            log.trace { "moment is empty. moments=$moments, limits=$limits, periods=$periods" }
            TimePeriodCollection(limits)
        } else {
            val range = TimeRange(mapPeriodStart(limits.start), mapPeriodEnd(limits.end))
            TimeLines.calculateGap(moments, range)
        }
    }

    private suspend fun timeLineMoments(periods: Collection<ITimePeriod>): ITimeLineMomentCollection {
        val moments = TimeLineMomentCollection()
        if (periods.isEmpty()) {
            log.trace { "specified eriods is empty" }
            return moments
        }

        // setup Gap
        val intersections = TimePeriodCollection()

        periods
            .asFlow()
            .buffer()
            .filterNot { it.isMoment }
            .collect { p ->
                val intersection = limits.intersectRange(p)
                // log.trace { "intersection=$intersection" }
                if (intersection != null && !intersection.isMoment) {
                    if (mapper != null) {
                        intersection.setup(mapPeriodStart(intersection.start), mapPeriodEnd(intersection.end))
                    }
                    intersections.add(intersection)
                }
            }

        if (intersections.isNotEmpty()) {
            moments.addAll(intersections)
        }
        return moments
    }

    private fun mapPeriodStart(start: ZonedDateTime): ZonedDateTime =
        mapper?.unmapStart(start) ?: start

    private fun mapPeriodEnd(end: ZonedDateTime): ZonedDateTime =
        mapper?.unmapEnd(end) ?: end
}
