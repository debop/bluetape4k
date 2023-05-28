package io.bluetape4k.utils.times.period.timelines

import io.bluetape4k.core.ValueObject
import io.bluetape4k.utils.times.period.ITimePeriod
import io.bluetape4k.utils.times.period.ITimePeriodCollection
import io.bluetape4k.utils.times.period.ITimePeriodContainer
import io.bluetape4k.utils.times.period.ITimePeriodMapper

/**
 * [ITimePeriod] 컬렉션을 가지며, 이를 통해 여러 기간에 대한 Union, Intersection, Gap 등을 구할 수 있도록 합니다.
 */
interface ITimeLine: ValueObject {

    val periods: ITimePeriodContainer

    val limits: ITimePeriod

    val mapper: ITimePeriodMapper?

    suspend fun combinePeriods(): ITimePeriodCollection

    suspend fun intersectPeriods(): ITimePeriodCollection

    suspend fun calculateGaps(): ITimePeriodCollection

}
