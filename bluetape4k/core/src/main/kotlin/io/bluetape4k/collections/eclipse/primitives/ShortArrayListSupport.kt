package io.bluetape4k.collections.eclipse.primitives

import io.bluetape4k.collections.eclipse.toFastList
import io.bluetape4k.collections.eclipse.toUnifiedSet
import io.bluetape4k.support.assertZeroOrPositiveNumber
import org.eclipse.collections.api.ShortIterable
import org.eclipse.collections.impl.list.mutable.FastList
import org.eclipse.collections.impl.list.mutable.primitive.ShortArrayList
import org.eclipse.collections.impl.set.mutable.UnifiedSet

fun ShortArray.toShortArrayList(): ShortArrayList = ShortArrayList.newListWith(*this)

fun Sequence<Short>.toShortArrayList(): ShortArrayList = ShortArrayList().also { list ->
    forEach { list.add(it) }
}

fun Iterable<Short>.toShortArrayList(): ShortArrayList = ShortArrayList().also { list ->
    forEach { list.add(it) }
}

inline fun shortArrayList(size: Int, initializer: (Int) -> Short): ShortArrayList {
    size.assertZeroOrPositiveNumber("size")
    val array = ShortArrayList(size)
    repeat(size) {
        array.add(initializer(it))
    }
    return array
}

fun shortArrayListOf(vararg elements: Short): ShortArrayList = ShortArrayList.newListWith(*elements)
fun shortArrayListOf(elements: Iterable<Short>): ShortArrayList = elements.toShortArrayList()

fun ShortIterable.asSequence(): Sequence<Short> = sequence {
    val iter = shortIterator()
    while (iter.hasNext()) {
        yield(iter.next())
    }
}

fun ShortIterable.asIterator(): Iterator<Short> = asSequence().iterator()

fun ShortIterable.asIterable(): Iterable<Short> = Iterable { asIterator().iterator() }
fun ShortIterable.asList(): List<Short> = asIterable().toList()
fun ShortIterable.asMutableList(): MutableList<Short> = asIterable().toMutableList()
fun ShortIterable.asSet(): Set<Short> = asIterable().toSet()
fun ShortIterable.asMutableSet(): MutableSet<Short> = asIterable().toMutableSet()

fun ShortIterable.asFastList(): FastList<Short> = asIterable().toFastList()
fun ShortIterable.asUnifiedSet(): UnifiedSet<Short> = asIterable().toUnifiedSet()

val ShortIterable.lastIndex: Int get() = size() - 1

fun ShortIterable.maxOrNull(): Short? = if (isEmpty) null else max()
fun ShortIterable.minOrNull(): Short? = if (isEmpty) null else min()

fun ShortIterable.product(): Double = asIterable().fold(1.0) { acc, i -> acc * i }
