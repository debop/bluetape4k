package io.bluetape4k.ranges

fun <T: Comparable<T>> Range<T>.contains(other: Range<T>): Boolean =
    first <= other.first && last >= other.last

fun <T: Comparable<T>> Iterable<Range<T>>.isAscending(): Boolean {
    val first = firstOrNull() ?: return true
    var max = first.first

    val count = drop(1)
        .takeWhile { range ->
            (max <= range.first).apply { max = range.first }
        }.count()

    return this.count() == count
}
