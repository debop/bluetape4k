package io.bluetape4k.ranges

import io.bluetape4k.ranges.impl.DefaultOpenClosedRange

fun <T: Comparable<T>> ClosedRange<T>.toOpenClosedRange(): OpenClosedRange<T> =
    DefaultOpenClosedRange(start, endInclusive)

fun <T: Comparable<T>> Range<T>.toOpenClosedRange(): OpenClosedRange<T> =
    DefaultOpenClosedRange(first, last)
