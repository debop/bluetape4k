package io.bluetape4k.ranges

fun <T: Comparable<T>> ClosedRange<T>.contains(other: ClosedRange<T>): Boolean =
    start <= other.start && endInclusive >= other.endInclusive


fun <T: Comparable<T>> Iterable<ClosedRange<T>>.isAscending(): Boolean {
    val first = firstOrNull() ?: return true

    var max = first.start
    return drop(1).fold(true) { isAscending, elem ->
        val newAscending = isAscending && (max <= elem.start)
        max = maxOf(max, elem.start)
        newAscending
    }
}
