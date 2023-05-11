package io.bluetape4k.collections.eclipse.primitives

import io.bluetape4k.collections.eclipse.toFastList
import io.bluetape4k.collections.eclipse.toUnifiedSet
import io.bluetape4k.core.assertZeroOrPositiveNumber
import org.eclipse.collections.api.IntIterable
import org.eclipse.collections.impl.list.mutable.FastList
import org.eclipse.collections.impl.list.mutable.primitive.IntArrayList
import org.eclipse.collections.impl.set.mutable.UnifiedSet

fun IntArray.toIntArrayList(): IntArrayList = IntArrayList.newListWith(*this)

fun Sequence<Int>.toIntArrayList(): IntArrayList =
    IntArrayList().also { array ->
        forEach { array.add(it) }
    }

fun Iterable<Int>.toIntArrayList(): IntArrayList =
    IntArrayList().also { array ->
        forEach { array.add(it) }
    }

inline fun intArrayList(
    size: Int,
    @BuilderInference initializer: (Int) -> Int = { it },
): IntArrayList {
    size.assertZeroOrPositiveNumber("size")

    val array = IntArrayList(size)
    repeat(size) {
        array.add(initializer(it))
    }
    return array
}

fun intArrayListOf(vararg elements: Int): IntArrayList = IntArrayList.newListWith(*elements)
fun intArrayListOf(elements: Iterable<Int>): IntArrayList = elements.toIntArrayList()

fun IntIterable.asSequence(): Sequence<Int> = sequence {
    val iter = intIterator()
    while (iter.hasNext()) {
        yield(iter.next())
    }
}

fun IntIterable.asIterator(): Iterator<Int> = asSequence().iterator()

fun IntIterable.asIterable(): Iterable<Int> = Iterable { asIterator().iterator() }
fun IntIterable.asList(): List<Int> = asIterable().toList()
fun IntIterable.asMutableList(): MutableList<Int> = asIterable().toMutableList()
fun IntIterable.asSet(): Set<Int> = asIterable().toSet()
fun IntIterable.asMutableSet(): MutableSet<Int> = asIterable().toMutableSet()

fun IntIterable.asFastList(): FastList<Int> = asIterable().toFastList()
fun IntIterable.asUnifiedSet(): UnifiedSet<Int> = asIterable().toUnifiedSet()

fun IntIterable.maxOrNull(): Int? = if (isEmpty) null else max()
fun IntIterable.minOrNull(): Int? = if (isEmpty) null else min()

val IntIterable.lastIndex: Int get() = size() - 1

fun IntIterable.lastOrNull(): Int? = when (this) {
    is IntArrayList -> if (isEmpty) null else this.last
    else            -> {
        val iterator = intIterator()
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

fun IntIterable.product(): Double = asIterable().fold(1.0) { acc, i -> acc * i }
