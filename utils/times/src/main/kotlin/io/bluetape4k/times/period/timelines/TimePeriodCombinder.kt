package io.bluetape4k.times.period.timelines

import io.bluetape4k.times.period.ITimePeriod
import io.bluetape4k.times.period.ITimePeriodCollection
import io.bluetape4k.times.period.ITimePeriodMapper
import io.bluetape4k.times.period.TimePeriodCollection

/**
 * TimePeriodCombiner
 */
class TimePeriodCombiner<T: ITimePeriod> @JvmOverloads constructor(val mapper: ITimePeriodMapper? = null) {

    fun combinePeriods(periods: Collection<ITimePeriod>): ITimePeriodCollection {
        return TimeLine<T>(TimePeriodCollection.ofAll(periods), null, mapper).combinePeriods()
    }
}
