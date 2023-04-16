package io.bluetape4k.utils.times.period.ranges

import io.bluetape4k.core.AbstractValueObject
import io.bluetape4k.core.ToStringBuilder
import io.bluetape4k.logging.KLogging
import java.time.Month

/**
 * 한 해에 속한 월의 범위를 나타내는 클래스
 *
 * @property startMonth 시작 월
 * @property endMonth 마지막 월
 */
open class MonthRangeInYear(
    val startMonth: Month = Month.JANUARY,
    val endMonth: Month = Month.DECEMBER,
): AbstractValueObject(), Comparable<MonthRangeInYear> {

    companion object: KLogging() {
        operator fun invoke(
            startMonthOfYear: Int,
            endMonthOfYear: Int,
        ): MonthRangeInYear {
            return MonthRangeInYear(Month.of(startMonthOfYear), Month.of(endMonthOfYear))
        }
    }

    init {
        check(startMonth <= endMonth) {
            "startMonth[$startMonth] must be less than or equals endMonth[$endMonth]"
        }
    }

    val isSingleMonth: Boolean get() = startMonth == endMonth

    val startMonthOfYear: Int = startMonth.value

    val endMonthOfYear: Int = endMonth.value

    fun hasInside(month: Month): Boolean = month in startMonth..endMonth

    override fun compareTo(other: MonthRangeInYear): Int =
        startMonth.compareTo(other.startMonth)

    override fun hashCode(): Int = startMonth.value + endMonth.value * 100

    override fun equalProperties(other: Any): Boolean {
        return other is MonthRangeInYear &&
            startMonth == other.startMonth &&
            endMonth == other.endMonth
    }

    override fun buildStringHelper(): ToStringBuilder {
        return super.buildStringHelper()
            .add("startMonth", startMonth)
            .add("endMonth", endMonth)
    }
}
