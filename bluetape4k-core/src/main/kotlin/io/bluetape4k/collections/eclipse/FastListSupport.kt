package io.bluetape4k.collections.eclipse

import io.bluetape4k.collections.asIterable
import io.bluetape4k.core.assertZeroOrPositiveNumber
import org.eclipse.collections.impl.list.mutable.FastList

fun <T> emptyFastList(): FastList<T> = FastList.newList()

inline fun <T> FastList(size: Int, initializer: (index: Int) -> T): FastList<T> {
    size.assertZeroOrPositiveNumber("size")

    val list = FastList.newList<T>(size)
    repeat(size) {
        list.add(initializer(it))
    }
    return list
}

@Deprecated("use FastList", ReplaceWith("FastList(size, initializer)"))
inline fun <T> fastList(size: Int, initializer: (index: Int) -> T): FastList<T> {
    size.assertZeroOrPositiveNumber("size")

    val list = FastList.newList<T>(size)
    repeat(size) {
        list.add(initializer(it))
    }
    return list
}

fun <T> fastList(capacity: Int): FastList<T> = FastList.newList(capacity)

fun <T> fastListOf(source: Iterable<T>): FastList<T> = FastList.newList(source)
fun <T> fastListOf(source: Sequence<T>): FastList<T> = FastList.newList(source.asIterable())
fun <T> fastListOf(vararg elements: T): FastList<T> = FastList.newListWith(*elements)

fun <T> Iterable<T>.toFastList(): FastList<T> = when (this) {
    is FastList<T> -> this
    else           -> FastList.newList(this)
}

fun <T> Sequence<T>.toFastList(): FastList<T> = fastListOf(asIterable())
fun <T> Iterator<T>.toFastList(): FastList<T> = fastListOf(asIterable())
fun <T> Array<out T>.toFastList(): FastList<T> = fastListOf(*this)
