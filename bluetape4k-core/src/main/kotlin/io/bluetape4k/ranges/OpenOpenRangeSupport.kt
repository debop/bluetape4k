package io.bluetape4k.ranges

import io.bluetape4k.ranges.impl.DefaultOpenOpenRange

fun <T: Comparable<T>> ClosedRange<T>.toOpenOpenRange(): OpenOpenRange<T> =
    DefaultOpenOpenRange(start, endInclusive)

fun <T: Comparable<T>> Range<T>.toOpenOpenRange(): OpenOpenRange<T> =
    DefaultOpenOpenRange(first, last)
