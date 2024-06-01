package io.bluetape4k.times.period.timelines

import io.bluetape4k.core.ValueObject
import io.bluetape4k.times.period.ITimePeriod
import io.bluetape4k.times.period.ITimePeriodCollection
import io.bluetape4k.times.period.ITimePeriodContainer
import io.bluetape4k.times.period.ITimePeriodMapper

/**
 * [ITimePeriod] 컬렉션을 가지며, 이를 통해 여러 기간에 대한 Union, Intersection, Gap 등을 구할 수 있도록 합니다.
 */
interface ITimeLine: ValueObject {

    val periods: ITimePeriodContainer

    val limits: ITimePeriod

    val mapper: ITimePeriodMapper?

    fun combinePeriods(): ITimePeriodCollection

    fun intersectPeriods(): ITimePeriodCollection

    fun calculateGaps(): ITimePeriodCollection

}
