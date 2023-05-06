package io.bluetape4k.collections.eclipse.multi

import io.bluetape4k.collections.eclipse.fastListOf
import org.eclipse.collections.impl.map.sorted.mutable.TreeSortedMap

typealias TreeMultimap<K, V> = TreeSortedMap<K, MutableList<V>>

fun <K, V> TreeMultimap<K, V>.valueSize(): Int = this.valuesView().sumOf { it.size }

inline fun <K: Comparable<K>, V> Iterable<V>.toTreeMultimap(keySelector: (V) -> K): TreeMultimap<K, V> {
    val map = TreeMultimap<K, V>()
    forEach { elem ->
        map.getIfAbsentPut(keySelector(elem), fastListOf<V>()).add(elem)
    }
    return map
}

inline fun <K: Comparable<K>, V> Iterable<V>.toTreeMultimap(
    comparator: Comparator<K>,
    keySelector: (V) -> K,
): TreeMultimap<K, V> {
    val map = TreeMultimap<K, V>(comparator)
    forEach { elem ->
        map.getIfAbsentPut(keySelector(elem), fastListOf<V>()).add(elem)
    }
    return map
}

inline fun <E, K: Comparable<K>, V> Iterable<E>.toTreeMultimap(
    keySelector: (E) -> K,
    valueSelector: (E) -> V,
): TreeMultimap<K, V> {
    val map = TreeMultimap<K, V>()
    forEach { elem ->
        map.getIfAbsentPut(keySelector(elem), fastListOf<V>()).add(valueSelector(elem))
    }
    return map
}

inline fun <E, K: Comparable<K>, V> Iterable<E>.toTreeMultimap(
    comparator: Comparator<K>,
    keySelector: (E) -> K,
    valueSelector: (E) -> V,
): TreeMultimap<K, V> {
    val map = TreeMultimap<K, V>(comparator)
    forEach { elem ->
        map.getIfAbsentPut(keySelector(elem), fastListOf<V>()).add(valueSelector(elem))
    }
    return map
}
