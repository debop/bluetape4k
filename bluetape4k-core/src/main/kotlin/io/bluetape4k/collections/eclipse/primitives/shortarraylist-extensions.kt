package io.bluetape4k.collections.eclipse.primitives

import org.eclipse.collections.api.ShortIterable
import org.eclipse.collections.impl.list.mutable.primitive.ShortArrayList

fun ShortArray.toShortArrayList(): ShortArrayList = ShortArrayList.newListWith(*this)

fun Sequence<Short>.toShortArrayList(): ShortArrayList =
    ShortArrayList().also { array ->
        forEach { array.add(it) }
    }

fun Iterable<Short>.toShortArrayList(): ShortArrayList =
    ShortArrayList().also { array ->
        forEach { array.add(it) }
    }

inline fun shortArrayList(size: Int, initializer: (Int) -> Short): ShortArrayList {
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

fun ShortIterable.maxOrNull(): Short? = if (isEmpty) null else max()
fun ShortIterable.minOrNull(): Short? = if (isEmpty) null else min()

fun ShortIterable.product(): Double = asIterable().fold(1.0) { acc, i -> acc * i }
