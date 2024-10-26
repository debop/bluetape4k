package io.bluetape4k.collections.eclipse.primitives

import io.bluetape4k.collections.eclipse.toFastList
import io.bluetape4k.collections.eclipse.toUnifiedSet
import io.bluetape4k.support.assertZeroOrPositiveNumber
import org.eclipse.collections.api.FloatIterable
import org.eclipse.collections.impl.list.mutable.FastList
import org.eclipse.collections.impl.list.mutable.primitive.FloatArrayList
import org.eclipse.collections.impl.set.mutable.UnifiedSet

fun FloatArray.toFloatArrayList(): FloatArrayList = FloatArrayList.newListWith(*this)

fun Sequence<Float>.toFloatArrayList(): FloatArrayList = FloatArrayList().also { list ->
    forEach { list.add(it) }
}

fun Iterable<Float>.toFloatArrayList(): FloatArrayList = FloatArrayList().also { list ->
    forEach { list.add(it) }
}

inline fun floatArrayList(size: Int, initializer: (Int) -> Float): FloatArrayList {
    size.assertZeroOrPositiveNumber("size")

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

fun FloatIterable.asFastList(): FastList<Float> = asIterable().toFastList()
fun FloatIterable.asUnifiedSet(): UnifiedSet<Float> = asIterable().toUnifiedSet()

fun FloatIterable.maxOrNull(): Float? = if (isEmpty) null else max()
fun FloatIterable.minOrNull(): Float? = if (isEmpty) null else min()

val FloatIterable.lastIndex: Int get() = size() - 1

fun FloatIterable.lastOrNull(): Float? = when (this) {
    is FloatArrayList -> if (isEmpty) null else this.last
    else              -> {
        val iterator = floatIterator()
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

fun FloatIterable.product(): Double = asIterable().fold(1.0) { acc, i -> acc * i }
