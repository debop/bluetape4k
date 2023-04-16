package io.bluetape4k.ranges

import io.bluetape4k.ranges.impl.DefaultClosedOpenRange

infix fun <T: Comparable<T>> T.until(endExclusive: T): ClosedOpenRange<T> =
    DefaultClosedOpenRange(this, endExclusive)

fun <T: Comparable<T>> ClosedRange<T>.toClosedOpenRange(): ClosedOpenRange<T> =
    DefaultClosedOpenRange(start, endInclusive)

fun <T: Comparable<T>> Range<T>.toClosedOpenRange(): ClosedOpenRange<T> =
    DefaultClosedOpenRange(first, last)
