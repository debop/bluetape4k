package io.bluetape4k.collections.eclipse

import io.bluetape4k.collections.asIterable
import io.bluetape4k.support.assertZeroOrPositiveNumber
import org.eclipse.collections.api.factory.SortedSets
import org.eclipse.collections.api.set.sorted.ImmutableSortedSet
import org.eclipse.collections.impl.set.sorted.mutable.TreeSortedSet

fun <T> emptyTreeSortedSet(): ImmutableSortedSet<T> = SortedSets.immutable.empty()

inline fun <T> treeSortedSet(size: Int, initializer: (Int) -> T): TreeSortedSet<T> {
    size.assertZeroOrPositiveNumber("size")
    return TreeSortedSet(List(size, initializer))
}

fun <T> treeSortedSetOf(source: Iterable<T>): TreeSortedSet<T> = TreeSortedSet.newSet(source)
fun <T> treeSortedSetOf(source: Sequence<T>): TreeSortedSet<T> = treeSortedSetOf(source.asIterable())
fun <T> treeSortedSetOf(vararg elements: T): TreeSortedSet<T> = TreeSortedSet.newSetWith(*elements)

fun <T> Iterable<T>.toTreeSortedSet(): TreeSortedSet<T> = when (this) {
    is TreeSortedSet<T> -> this
    else                -> TreeSortedSet.newSet(this)
}

fun <T> Sequence<T>.toTreeSortedSet(): TreeSortedSet<T> = treeSortedSetOf(asIterable())
fun <T> Iterator<T>.toTreeSortedSet(): TreeSortedSet<T> = treeSortedSetOf(asIterable())
fun <T> Array<out T>.toTreeSortedSet(): TreeSortedSet<T> = treeSortedSetOf(*this)
