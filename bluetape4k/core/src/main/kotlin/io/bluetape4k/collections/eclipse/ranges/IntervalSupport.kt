package io.bluetape4k.collections.eclipse.ranges

import io.bluetape4k.collections.eclipse.primitives.longArrayList
import io.bluetape4k.core.requirePositiveNumber
import org.eclipse.collections.api.list.ImmutableList
import org.eclipse.collections.impl.list.mutable.primitive.IntArrayList
import org.eclipse.collections.impl.list.mutable.primitive.LongArrayList
import org.eclipse.collections.impl.list.primitive.IntInterval

fun intIntervalOf(start: Int, endInclusive: Int, step: Int = 1): IntInterval =
    IntInterval.fromToBy(start, endInclusive, step)

fun IntInterval.toIntArrayList(): IntArrayList = IntArrayList.newList(this)
fun IntInterval.toLongArrayList(): LongArrayList = longArrayList(this.size()) { it.toLong() }

inline fun IntInterval.forEach(crossinline block: (Int) -> Unit) {
    this.each { block(it) }
}

inline fun <T> IntInterval.map(crossinline mapper: (Int) -> T): ImmutableList<T> {
    return this.collect { mapper(it) }
}

fun IntInterval.grouped(groupSize: Int = 1): Sequence<IntArrayList> = sequence {
    groupSize.requirePositiveNumber("groupSize")

    this@grouped
        .chunk(groupSize)
        .forEach { chunked ->
            yield(IntArrayList.newList(chunked))
        }
}
