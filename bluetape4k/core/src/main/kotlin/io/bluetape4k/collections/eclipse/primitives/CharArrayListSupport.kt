package io.bluetape4k.collections.eclipse.primitives

import io.bluetape4k.collections.eclipse.toFastList
import io.bluetape4k.collections.eclipse.toUnifiedSet
import io.bluetape4k.core.assertZeroOrPositiveNumber
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

fun CharArrayList.asSequence(): Sequence<Char> = sequence {
    val iter = charIterator()
    while (iter.hasNext()) {
        yield(iter.next())
    }
}

fun CharArrayList.asIterator(): Iterator<Char> = asSequence().iterator()

fun CharArrayList.asIterable(): Iterable<Char> = Iterable { asIterator().iterator() }
fun CharArrayList.asList(): List<Char> = asIterable().toList()
fun CharArrayList.asMutableList(): MutableList<Char> = asIterable().toMutableList()
fun CharArrayList.asSet(): Set<Char> = asIterable().toSet()
fun CharArrayList.asMutableSet(): MutableSet<Char> = asIterable().toMutableSet()

fun CharArrayList.asFastList(): FastList<Char> = asIterable().toFastList()
fun CharArrayList.asUnifiedSet(): UnifiedSet<Char> = asIterable().toUnifiedSet()

fun CharArrayList.maxOrNull(): Char? = if (isEmpty) null else max()
fun CharArrayList.minOrNull(): Char? = if (isEmpty) null else min()
