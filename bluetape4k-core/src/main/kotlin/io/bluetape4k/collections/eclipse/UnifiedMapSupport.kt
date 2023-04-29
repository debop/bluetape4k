package io.bluetape4k.collections.eclipse

import io.bluetape4k.collections.asIterable
import io.bluetape4k.core.assertZeroOrPositiveNumber
import org.eclipse.collections.api.factory.Maps
import org.eclipse.collections.api.map.ImmutableMap
import org.eclipse.collections.impl.map.mutable.UnifiedMap
import org.eclipse.collections.impl.tuple.Tuples

fun <K, V> emptyUnifiedMapOf(): ImmutableMap<K, V> = Maps.immutable.empty()

inline fun <K, V> UnifiedMap(size: Int, initializer: (Int) -> Pair<K, V>): UnifiedMap<K, V> {
    size.assertZeroOrPositiveNumber("size")

    return UnifiedMap.newMap<K, V>(size).apply {
        List(size) {
            val pair = initializer(it)
            put(pair.first, pair.second)
        }
    }
}

fun <K, V> unifiedMapOf(vararg pairs: Pair<K, V>): UnifiedMap<K, V> =
    UnifiedMap.newMapWith(pairs.map { Tuples.pair(it.first, it.second) })

fun <K, V> unifiedMapOf(elements: Iterable<Pair<K, V>>): UnifiedMap<K, V> =
    UnifiedMap.newMap(elements.map { it.first to it.second }.toMap())


fun <K, V> unifiedMapWithCapacity(capacity: Int): UnifiedMap<K, V> =
    UnifiedMap.newMap(capacity)

fun <K, V> Map<K, V>.toUnifiedMap(): UnifiedMap<K, V> = when (this) {
    is UnifiedMap<K, V> -> this
    else                -> UnifiedMap.newMap(this)
}

fun <K, V> Iterable<Pair<K, V>>.toUnifiedMap(): UnifiedMap<K, V> = unifiedMapOf(this)
fun <K, V> Sequence<Pair<K, V>>.toUnifiedMap(): UnifiedMap<K, V> = unifiedMapOf(this.asIterable())
fun <K, V> Iterator<Pair<K, V>>.toUnifiedMap(): UnifiedMap<K, V> = unifiedMapOf(this.asIterable())
