package io.bluetape4k.utils.times

import java.util.Date

/**
 * [Date]를 열거하는 Iterator 입니다.
 *
 * @param T [Date]의 하위 클래스
 */
abstract class DateIterator<out T: Date>: Iterator<T> {

    abstract fun nextDate(): T

    final override fun next(): T = nextDate()
}
