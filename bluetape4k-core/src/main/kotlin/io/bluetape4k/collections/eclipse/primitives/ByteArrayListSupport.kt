package io.bluetape4k.collections.eclipse.primitives

import io.bluetape4k.collections.eclipse.toFastList
import io.bluetape4k.collections.eclipse.toUnifiedSet
import org.eclipse.collections.api.ByteIterable
import org.eclipse.collections.impl.list.mutable.FastList
import org.eclipse.collections.impl.list.mutable.primitive.ByteArrayList
import org.eclipse.collections.impl.set.mutable.UnifiedSet

fun ByteArray.toByteArrayList(): ByteArrayList = ByteArrayList.newListWith(*this)

fun Sequence<Byte>.toByteArrayList(): ByteArrayList =
    ByteArrayList().also { array ->
        forEach { array.add(it) }
    }

fun Iterable<Byte>.toByteArrayList(): ByteArrayList =
    ByteArrayList().also { array ->
        forEach { array.add(it) }
    }

inline fun byteArrayList(size: Int, initializer: (Int) -> Byte): ByteArrayList {
    val array = ByteArrayList(size)
    repeat(size) {
        array.add(initializer(it))
    }
    return array
}

fun byteArrayListOf(vararg elements: Byte): ByteArrayList = ByteArrayList.newListWith(*elements)
fun byteArrayListOf(elements: Iterable<Byte>): ByteArrayList = elements.toByteArrayList()

fun ByteArrayList.asSequence(): Sequence<Byte> = sequence {
    val iter = byteIterator()
    while (iter.hasNext()) {
        yield(iter.next())
    }
}

fun ByteArrayList.asIterator(): Iterator<Byte> = iterator {
    val iter = byteIterator()
    while (iter.hasNext()) {
        yield(iter.next())
    }
}

fun ByteIterable.asSequence(): Sequence<Byte> = sequence {
    val iter = byteIterator()
    while (iter.hasNext()) {
        yield(iter.next())
    }
}

fun ByteIterable.asIterator(): Iterator<Byte> = asSequence().iterator()

fun ByteArrayList.asIterable(): Iterable<Byte> = Iterable { asIterator().iterator() }
fun ByteArrayList.asList(): List<Byte> = asIterable().toList()
fun ByteArrayList.asMutableList(): MutableList<Byte> = asIterable().toMutableList()
fun ByteArrayList.asSet(): Set<Byte> = asIterable().toSet()
fun ByteArrayList.asMutableSet(): MutableSet<Byte> = asIterable().toMutableSet()

fun ByteArrayList.asFastList(): FastList<Byte> = asIterable().toFastList()
fun ByteArrayList.asUnifiedSet(): UnifiedSet<Byte> = asIterable().toUnifiedSet()

fun ByteArrayList.maxOrNull(): Byte? = if (isEmpty) null else max()
fun ByteArrayList.minOrNull(): Byte? = if (isEmpty) null else min()
