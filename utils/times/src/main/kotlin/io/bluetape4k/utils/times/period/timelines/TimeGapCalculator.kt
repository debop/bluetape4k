package io.bluetape4k.utils.times.period.timelines

import io.bluetape4k.utils.times.period.ITimePeriod
import io.bluetape4k.utils.times.period.ITimePeriodCollection
import io.bluetape4k.utils.times.period.ITimePeriodContainer
import io.bluetape4k.utils.times.period.ITimePeriodMapper

open class TimeGapCalculator<T : ITimePeriod>(val mapper: ITimePeriodMapper? = null) {

    suspend fun gaps(excludePeriods: ITimePeriodContainer, limits: ITimePeriod? = null): ITimePeriodCollection =
        TimeLine<T>(excludePeriods, limits, mapper).calculateGaps()

}
