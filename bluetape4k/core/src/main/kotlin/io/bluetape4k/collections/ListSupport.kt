package io.bluetape4k.collections

/**
 * IntRange를 List<Int>로 변환합니다.
 */
fun intRangeOf(range: IntRange): List<Int> = range.toList()

/**
 * LongRange를 List<Long>로 변환합니다.
 */
fun longRangeOf(range: LongRange): List<Long> = range.toList()

/**
 * 시작 값과 개수로 IntRange를 List<Int>로 변환합니다.
 */
fun intRangeOf(start: Int, count: Int): List<Int> = intRangeOf(start..<start + count)

/**
 * 시작 값과 개수로 LongRange를 List<Long>로 변환합니다.
 */
fun longRangeOf(start: Long, count: Int): List<Long> = longRangeOf(start..<start + count)
