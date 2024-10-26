package io.bluetape4k.collections.eclipse.primitives

import io.bluetape4k.collections.eclipse.toFastList
import io.bluetape4k.collections.eclipse.toUnifiedSet
import io.bluetape4k.support.assertZeroOrPositiveNumber
import org.eclipse.collections.api.LongIterable
import org.eclipse.collections.impl.list.mutable.FastList
import org.eclipse.collections.impl.list.mutable.primitive.LongArrayList
import org.eclipse.collections.impl.set.mutable.UnifiedSet

fun LongArray.toLongArrayList(): LongArrayList = LongArrayList.newListWith(*this)

fun Sequence<Long>.toLongArrayList(): LongArrayList = LongArrayList().also { list ->
    forEach { list.add(it) }
}

fun Iterable<Long>.toLongArrayList(): LongArrayList = LongArrayList().also { list ->
    forEach { list.add(it) }
}

inline fun longArrayList(size: Int, initializer: (Int) -> Long): LongArrayList {
    size.assertZeroOrPositiveNumber("size")

    val array = LongArrayList(size)
    repeat(size) {
        array.add(initializer(it))
    }
    return array
}

fun longArrayListOf(vararg elements: Long): LongArrayList = LongArrayList.newListWith(*elements)
fun longArrayListOf(elements: Iterable<Long>): LongArrayList = elements.toLongArrayList()
fun longArrayListOf(longs: LongIterable): LongArrayList = LongArrayList.newList(longs)

fun LongIterable.asLongArrayList(): LongArrayList = longArrayListOf(this)

fun LongIterable.asSequence(): Sequence<Long> = sequence {
    val iter = longIterator()
    while (iter.hasNext()) {
        yield(iter.next())
    }
}

fun LongIterable.asIterator(): Iterator<Long> = asSequence().iterator()

fun LongIterable.asIterable(): Iterable<Long> = Iterable { asIterator().iterator() }
fun LongIterable.asList(): List<Long> = asIterable().toList()
fun LongIterable.asMutableList(): MutableList<Long> = asIterable().toMutableList()
fun LongIterable.asSet(): Set<Long> = asIterable().toSet()
fun LongIterable.asMutableSet(): MutableSet<Long> = asIterable().toMutableSet()

fun LongIterable.asFastList(): FastList<Long> = asIterable().toFastList()
fun LongIterable.asUnifiedSet(): UnifiedSet<Long> = asIterable().toUnifiedSet()

fun LongIterable.maxOrNull(): Long? = if (isEmpty) null else max()
fun LongIterable.minOrNull(): Long? = if (isEmpty) null else min()


val LongIterable.lastIndex: Int get() = size() - 1

fun LongIterable.lastOrNull(): Long? = when (this) {
    is LongArrayList -> if (isEmpty) null else this.last
    else             -> {
        val iterator = longIterator()
        if (!iterator.hasNext()) {
            null
        } else {
            var last = iterator.next()
            while (iterator.hasNext()) {
                last = iterator.next()
            }
            last
        }
    }
}


fun LongIterable.product(): Double = asIterable().fold(1.0) { acc, i -> acc * i }
