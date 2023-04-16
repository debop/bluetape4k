package io.bluetape4k.ranges

import io.bluetape4k.ranges.impl.DefaultClosedClosedRange

fun <T: Comparable<T>> Range<T>.toClosedClosedRange(): ClosedClosedRange<T> =
    DefaultClosedClosedRange(first, last)

fun <T: Comparable<T>> ClosedRange<T>.toClosedClosedRange(): ClosedClosedRange<T> =
    DefaultClosedClosedRange(start, endInclusive)
