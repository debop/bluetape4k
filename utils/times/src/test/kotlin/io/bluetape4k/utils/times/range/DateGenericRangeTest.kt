package io.bluetape4k.utils.times.range

import io.bluetape4k.utils.times.days
import io.bluetape4k.utils.times.hours
import io.bluetape4k.utils.times.minus
import io.bluetape4k.utils.times.plus
import java.sql.Timestamp
import java.util.Date
import org.amshove.kluent.shouldBeFalse
import org.amshove.kluent.shouldBeTrue
import org.junit.jupiter.api.Test

@Suppress("UNCHECKED_CAST")
abstract class DateGenericRangeTest<T: Date> {

    abstract val current: T

    private val nextDay: T by lazy { (current + 1.days()) as T }
    private val prevDay: T by lazy { (current - 1.days()) as T }

    @Test
    fun `progression contains`() {
        val range = DateGenericRange(current, nextDay)

        range.contains(current).shouldBeTrue()
        range.contains(nextDay).shouldBeTrue()

        range.containsDate(current + 1.hours()).shouldBeTrue()
        range.containsDate(current - 1.hours()).shouldBeFalse()
        range.containsDate(nextDay + 1.hours()).shouldBeFalse()
    }
}

class DateRangeTest: DateGenericRangeTest<Date>() {
    override val current: Date = Date()

}

class TimestampRangeTest: DateGenericRangeTest<Timestamp>() {
    override val current: Timestamp = Timestamp(Date().time)
}
