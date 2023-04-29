package io.bluetape4k.collections.eclipse.primitives

import io.bluetape4k.collections.eclipse.toFastList
import io.bluetape4k.collections.eclipse.toUnifiedSet
import io.bluetape4k.core.assertZeroOrPositiveNumber
import org.eclipse.collections.impl.list.mutable.FastList
import org.eclipse.collections.impl.list.mutable.primitive.BooleanArrayList
import org.eclipse.collections.impl.set.mutable.UnifiedSet

fun BooleanArray.toBooleanArrayList(): BooleanArrayList = BooleanArrayList.newListWith(*this)

fun Sequence<Boolean>.toBooleanArrayList(): BooleanArrayList =
    BooleanArrayList.newListWith(*this.toList().toBooleanArray())

fun Iterable<Boolean>.toBooleanArrayList(): BooleanArrayList =
    BooleanArrayList.newListWith(*this.toList().toBooleanArray())

inline fun BooleanArrayList(size: Int, initializer: (Int) -> Boolean): BooleanArrayList {
    size.assertZeroOrPositiveNumber("size")

    val array = BooleanArrayList(size)
    repeat(size) {
        array.add(initializer(it))
    }
    return array
}


fun booleanArrayListOf(vararg elements: Boolean): BooleanArrayList = BooleanArrayList.newListWith(*elements)
fun booleanArrayListOf(elements: Iterable<Boolean>): BooleanArrayList = elements.toBooleanArrayList()

fun BooleanArrayList.asSequence(): Sequence<Boolean> = sequence {
    val iter = booleanIterator()
    while (iter.hasNext()) {
        yield(iter.next())
    }
}

fun BooleanArrayList.asIterator(): Iterator<Boolean> = asSequence().iterator()

fun BooleanArrayList.asIterable(): Iterable<Boolean> = Iterable { asIterator().iterator() }
fun BooleanArrayList.asList(): List<Boolean> = asIterable().toList()
fun BooleanArrayList.asMutableList(): List<Boolean> = asIterable().toMutableList()
fun BooleanArrayList.asSet(): Set<Boolean> = asIterable().toSet()
fun BooleanArrayList.asMutableSet(): Set<Boolean> = asIterable().toMutableSet()

fun BooleanArrayList.asFastList(): FastList<Boolean> = asIterable().toFastList()
fun BooleanArrayList.asUnifiedSet(): UnifiedSet<Boolean> = asIterable().toUnifiedSet()
