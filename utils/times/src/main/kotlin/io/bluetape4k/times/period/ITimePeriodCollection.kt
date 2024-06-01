package io.bluetape4k.times.period

import java.time.ZonedDateTime

interface ITimePeriodCollection: ITimePeriodContainer {
    fun hasInsidePeriods(that: ITimePeriod): Boolean
    fun hasOverlapPeriods(that: ITimePeriod): Boolean

    fun hasIntersectionPeriods(moment: ZonedDateTime): Boolean
    fun hasIntersectionPeriods(that: ITimePeriod): Boolean

    fun insidePeriods(target: ITimePeriod): List<ITimePeriod>
    fun overlapPeriods(target: ITimePeriod): List<ITimePeriod>

    fun intersectionPeriod(moment: ZonedDateTime): List<ITimePeriod>
    fun intersectionPeriod(target: ITimePeriod): List<ITimePeriod>

    fun relationPeriods(target: ITimePeriod, vararg relations: PeriodRelation): List<ITimePeriod>
}
