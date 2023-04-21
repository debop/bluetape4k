package io.bluetape4k.collections.eclipse

import io.bluetape4k.collections.asIterable
import io.bluetape4k.core.assertZeroOrPositiveNumber
import org.eclipse.collections.api.factory.Sets
import org.eclipse.collections.api.set.ImmutableSet
import org.eclipse.collections.impl.set.mutable.UnifiedSet

fun <T> emptyUnifiedSet(): ImmutableSet<T> = Sets.immutable.empty()

inline fun <T> UnifiedSet(size: Int, initializer: (Int) -> T): UnifiedSet<T> {
    size.assertZeroOrPositiveNumber("size")
    return UnifiedSet.newSet(List(size, initializer))
}

@Deprecated("use UnifiedSet", ReplaceWith("UnifiedSet(size, initializer)"))
inline fun <T> unifiedSet(size: Int, initializer: (Int) -> T): UnifiedSet<T> {
    size.assertZeroOrPositiveNumber("size")
    return UnifiedSet.newSet(List(size, initializer))
}

fun <T> unifiedSetOf(source: Iterable<T>): UnifiedSet<T> = UnifiedSet.newSet(source)
fun <T> unifiedSetOf(source: Sequence<T>): UnifiedSet<T> = unifiedSetOf(source.asIterable())
fun <T> unifiedSetOf(vararg elements: T): UnifiedSet<T> = UnifiedSet.newSetWith(*elements)

fun <T> unifiedSetWithCapacity(size: Int): UnifiedSet<T> = UnifiedSet.newSet(size)

fun <T> Set<T>.toUnifiedSet(): UnifiedSet<T> = when (this) {
    is UnifiedSet<T> -> this
    else -> UnifiedSet.newSet(this)
}

fun <T> Iterable<T>.toUnifiedSet(): UnifiedSet<T> = unifiedSetOf(this)
fun <T> Sequence<T>.toUnifiedSet(): UnifiedSet<T> = unifiedSetOf(asIterable())
fun <T> Iterator<T>.toUnifiedSet(): UnifiedSet<T> = unifiedSetOf(asIterable())
fun <T> Array<out T>.toUnifiedSet(): UnifiedSet<T> = UnifiedSet.newSetWith(*this)
