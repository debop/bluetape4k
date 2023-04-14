package io.bluetape4k.collections.eclipse.primitives

import org.eclipse.collections.api.FloatIterable
import org.eclipse.collections.impl.list.mutable.primitive.FloatArrayList

fun FloatArray.toFloatArrayList(): FloatArrayList = FloatArrayList.newListWith(*this)

fun Sequence<Float>.toFloatArrayList(): FloatArrayList =
    FloatArrayList().also { array ->
        forEach { array.add(it) }
    }

fun Iterable<Float>.toFloatArrayList(): FloatArrayList =
    FloatArrayList().also { array ->
        forEach { array.add(it) }
    }

inline fun floatArrayList(size: Int, initializer: (Int) -> Float): FloatArrayList {
    val array = FloatArrayList(size)
    repeat(size) {
        array.add(initializer(it))
    }
    return array
}

fun floatArrayListOf(vararg elements: Float): FloatArrayList = FloatArrayList.newListWith(*elements)
fun floatArrayListOf(elements: Iterable<Float>): FloatArrayList = elements.toFloatArrayList()

fun FloatIterable.asSequence(): Sequence<Float> = sequence {
    val iter = floatIterator()
    while (iter.hasNext()) {
        yield(iter.next())
    }
}

fun FloatIterable.asIterator(): Iterator<Float> = asSequence().iterator()

fun FloatIterable.asIterable(): Iterable<Float> = Iterable { asIterator().iterator() }
fun FloatIterable.asList(): List<Float> = asIterable().toList()
fun FloatIterable.asMutableList(): MutableList<Float> = asIterable().toMutableList()
fun FloatIterable.asSet(): Set<Float> = asIterable().toSet()
fun FloatIterable.asMutableSet(): MutableSet<Float> = asIterable().toMutableSet()

fun FloatIterable.maxOrNull(): Float? = if (isEmpty) null else max()
fun FloatIterable.minOrNull(): Float? = if (isEmpty) null else min()

fun FloatIterable.product(): Double = asIterable().fold(1.0) { acc, i -> acc * i }