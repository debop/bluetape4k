package io.bluetape4k.collections.eclipse.primitives

import io.bluetape4k.collections.eclipse.toFastList
import io.bluetape4k.collections.eclipse.toUnifiedSet
import io.bluetape4k.core.assertZeroOrPositiveNumber
import org.eclipse.collections.api.CharIterable
import org.eclipse.collections.impl.list.mutable.FastList
import org.eclipse.collections.impl.list.mutable.primitive.CharArrayList
import org.eclipse.collections.impl.set.mutable.UnifiedSet

fun CharArray.toCharArrayList(): CharArrayList = CharArrayList.newListWith(*this)

fun Sequence<Char>.toCharArrayList(): CharArrayList = CharArrayList().also { list ->
    forEach { list.add(it) }
}

fun Iterable<Char>.toCharArrayList(): CharArrayList = CharArrayList().also { list ->
    forEach { list.add(it) }
}


inline fun charArrayList(size: Int, initializer: (Int) -> Char): CharArrayList {
    size.assertZeroOrPositiveNumber("size")

    val chars = CharArrayList(size)
    repeat(size) {
        chars.add(initializer(it))
    }
    return chars
}

fun charArrayListOf(vararg elements: Char): CharArrayList = CharArrayList.newListWith(*elements)
fun charArrayListOf(elements: Iterable<Char>): CharArrayList = elements.toCharArrayList()

fun CharIterable.asSequence(): Sequence<Char> = sequence {
    val iter = charIterator()
    while (iter.hasNext()) {
        yield(iter.next())
    }
}

fun CharIterable.asIterator(): Iterator<Char> = asSequence().iterator()

fun CharIterable.asIterable(): Iterable<Char> = Iterable { asIterator().iterator() }
fun CharIterable.asList(): List<Char> = asIterable().toList()
fun CharIterable.asMutableList(): MutableList<Char> = asIterable().toMutableList()
fun CharIterable.asSet(): Set<Char> = asIterable().toSet()
fun CharIterable.asMutableSet(): MutableSet<Char> = asIterable().toMutableSet()

fun CharIterable.asFastList(): FastList<Char> = asIterable().toFastList()
fun CharIterable.asUnifiedSet(): UnifiedSet<Char> = asIterable().toUnifiedSet()

fun CharIterable.maxOrNull(): Char? = if (isEmpty) null else max()
fun CharIterable.minOrNull(): Char? = if (isEmpty) null else min()

val CharIterable.lastIndex: Int get() = size() - 1
