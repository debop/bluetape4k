package io.bluetape4k.collections.eclipse.primitives

import io.bluetape4k.collections.eclipse.toFastList
import io.bluetape4k.collections.eclipse.toUnifiedSet
import io.bluetape4k.core.assertZeroOrPositiveNumber
import org.eclipse.collections.api.DoubleIterable
import org.eclipse.collections.impl.list.mutable.FastList
import org.eclipse.collections.impl.list.mutable.primitive.DoubleArrayList
import org.eclipse.collections.impl.set.mutable.UnifiedSet

fun DoubleArray.toDoubleArrayList(): DoubleArrayList = DoubleArrayList.newListWith(*this)

fun Sequence<Double>.toDoubleArrayList(): DoubleArrayList =
    DoubleArrayList().also { array ->
        forEach { array.add(it) }
    }

fun Iterable<Double>.toDoubleArrayList(): DoubleArrayList =
    DoubleArrayList().also { array ->
        forEach { array.add(it) }
    }

inline fun doubleArrayList(
    size: Int,
    @BuilderInference initializer: (Int) -> Double,
): DoubleArrayList {
    size.assertZeroOrPositiveNumber("size")

    val array = DoubleArrayList(size)
    repeat(size) {
        array.add(initializer(it))
    }
    return array
}

fun doubleArrayListOf(vararg elements: Double): DoubleArrayList = DoubleArrayList.newListWith(*elements)
fun doubleArrayListOf(elements: Iterable<Double>): DoubleArrayList = elements.toDoubleArrayList()

fun DoubleIterable.asSequence(): Sequence<Double> = sequence {
    val iter = doubleIterator()
    while (iter.hasNext()) {
        yield(iter.next())
    }
}

fun DoubleIterable.asIterator(): Iterator<Double> = asSequence().iterator()

fun DoubleIterable.asIterable(): Iterable<Double> = Iterable { asIterator().iterator() }
fun DoubleIterable.asList(): List<Double> = asIterable().toList()
fun DoubleIterable.asMutableList(): MutableList<Double> = asIterable().toMutableList()
fun DoubleIterable.asSet(): Set<Double> = asIterable().toSet()
fun DoubleIterable.asMutableSet(): MutableSet<Double> = asIterable().toMutableSet()

fun DoubleIterable.asFastList(): FastList<Double> = asIterable().toFastList()
fun DoubleIterable.asUnifiedSet(): UnifiedSet<Double> = asIterable().toUnifiedSet()

fun DoubleIterable.maxOrNull(): Double? = if (isEmpty) null else max()
fun DoubleIterable.minOrNull(): Double? = if (isEmpty) null else min()

fun DoubleIterable.product(): Double = asIterable().fold(1.0) { acc, i -> acc * i }
