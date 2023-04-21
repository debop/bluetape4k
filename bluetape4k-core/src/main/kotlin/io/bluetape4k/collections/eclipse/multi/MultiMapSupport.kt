package io.bluetape4k.collections.eclipse.multi

import io.bluetape4k.collections.eclipse.toTuplePair
import org.eclipse.collections.api.multimap.Multimap
import org.eclipse.collections.api.multimap.bag.BagMultimap
import org.eclipse.collections.api.multimap.bag.ImmutableBagMultimap
import org.eclipse.collections.api.multimap.bag.MutableBagMultimap
import org.eclipse.collections.api.multimap.list.ImmutableListMultimap
import org.eclipse.collections.api.multimap.list.ListMultimap
import org.eclipse.collections.api.multimap.list.MutableListMultimap
import org.eclipse.collections.api.multimap.set.ImmutableSetMultimap
import org.eclipse.collections.api.multimap.set.MutableSetMultimap
import org.eclipse.collections.api.multimap.set.SetMultimap
import org.eclipse.collections.impl.factory.Multimaps
import org.eclipse.collections.impl.multimap.bag.HashBagMultimap
import org.eclipse.collections.impl.multimap.list.FastListMultimap
import org.eclipse.collections.impl.multimap.set.UnifiedSetMultimap
import org.eclipse.collections.impl.tuple.Tuples

fun <K, V> emptyListMultimap(): ImmutableListMultimap<K, V> = Multimaps.immutable.list.empty()
fun <K, V> emptySetMultimap(): ImmutableSetMultimap<K, V> = Multimaps.immutable.set.empty()
fun <K, V> emptyBagMultimap(): ImmutableBagMultimap<K, V> = Multimaps.immutable.bag.empty()

fun <K, V> listMultimapOf(vararg pairs: Pair<K, V>): MutableListMultimap<K, V> =
    FastListMultimap.newMultimap(pairs.map { it.toTuplePair() })

fun <K, V> setMultimapOf(vararg pairs: Pair<K, V>): MutableSetMultimap<K, V> =
    UnifiedSetMultimap.newMultimap(pairs.map { it.toTuplePair() })

fun <K, V> bagMultimapOf(vararg pairs: Pair<K, V>): MutableBagMultimap<K, V> =
    HashBagMultimap.newMultimap(pairs.map { it.toTuplePair() })

fun <K, V> Map<K, V>.toListMultimap(): ListMultimap<K, V> =
    FastListMultimap.newMultimap(this.map { (k, v) -> Tuples.pair(k, v) })

fun <K, V> Map<K, V>.toMutableListMultimap(): MutableListMultimap<K, V> =
    FastListMultimap.newMultimap(this.map { (k, v) -> Tuples.pair(k, v) })

fun <K, V> Map<K, V>.toSetMultimap(): SetMultimap<K, V> =
    UnifiedSetMultimap.newMultimap(this.map { (k, v) -> Tuples.pair(k, v) })

fun <K, V> Map<K, V>.toMutableSetMultimap(): MutableSetMultimap<K, V> =
    UnifiedSetMultimap.newMultimap(this.map { (k, v) -> Tuples.pair(k, v) })

fun <K, V> Map<K, V>.toBagMultimap(): BagMultimap<K, V> =
    HashBagMultimap.newMultimap(this.map { (k, v) -> Tuples.pair(k, v) })

fun <K, V> Map<K, V>.toMutableBagMultimap(): MutableBagMultimap<K, V> =
    HashBagMultimap.newMultimap(this.map { (k, v) -> Tuples.pair(k, v) })


fun <K, V> Iterable<Pair<K, V>>.toListMultimap(): ListMultimap<K, V> =
    FastListMultimap.newMultimap<K, V>().apply {
        this@toListMultimap.forEach {
            //HINT: operator set을 사용하면 안됩니다.
            put(it.first, it.second)
        }
    }

fun <K, V> Iterable<Pair<K, V>>.toMutableListMultimap(): MutableListMultimap<K, V> =
    FastListMultimap.newMultimap<K, V>().apply {
        this@toMutableListMultimap.forEach {
            //HINT: operator set을 사용하면 안됩니다.
            put(it.first, it.second)
        }
    }

inline fun <T, K, V> Iterable<T>.toListMultimap(mapper: (T) -> Pair<K, V>): ListMultimap<K, V> =
    FastListMultimap.newMultimap(this.map { mapper(it).toTuplePair() })

inline fun <T, K, V> Iterable<T>.toMutableListMultimap(mapper: (T) -> Pair<K, V>): MutableListMultimap<K, V> =
    FastListMultimap.newMultimap(this.map { mapper(it).toTuplePair() })

fun <K, V> Iterable<Pair<K, V>>.toSetMultimap(): SetMultimap<K, V> =
    UnifiedSetMultimap.newMultimap<K, V>().apply {
        this@toSetMultimap.forEach {
            //HINT: operator set을 사용하면 안됩니다.
            put(it.first, it.second)
        }
    }

fun <K, V> Iterable<Pair<K, V>>.toMutableSetMultimap(): MutableSetMultimap<K, V> =
    UnifiedSetMultimap.newMultimap<K, V>().apply {
        this@toMutableSetMultimap.forEach {
            //HINT: operator set을 사용하면 안됩니다.
            put(it.first, it.second)
        }
    }

inline fun <T, K, V> Iterable<T>.toSetMultimap(mapper: (T) -> Pair<K, V>): SetMultimap<K, V> =
    UnifiedSetMultimap.newMultimap(this.map { mapper(it).toTuplePair() })

inline fun <T, K, V> Iterable<T>.toMutableSetMultimap(mapper: (T) -> Pair<K, V>): MutableSetMultimap<K, V> =
    UnifiedSetMultimap.newMultimap(this.map { mapper(it).toTuplePair() })


inline fun <K, V> Iterable<V>.groupByListMultimap(keySelector: (V) -> K): ListMultimap<K, V> =
    toListMultimap { keySelector(it) to it }

inline fun <K, V> Iterable<V>.groupByMutableListMultimap(keySelector: (V) -> K): MutableListMultimap<K, V> =
    toMutableListMultimap { keySelector(it) to it }

inline fun <K, V> Multimap<K, V>.filter(crossinline predicate: (K, Iterable<V>) -> Boolean): Multimap<K, V> =
    selectKeysMultiValues { key, values -> predicate(key, values) }

inline fun <K, V> Multimap<K, V>.listMap(mapper: (K, Iterable<V>) -> Pair<K, Iterable<V>>): ListMultimap<K, V> =
    keyMultiValuePairsView()
        .map { mapper(it.one, it.two) }
        .flatMap { pair -> pair.second.map { Pair(pair.first, it) } }
        .toListMultimap { it }

inline fun <K, V> Multimap<K, V>.setMap(mapper: (K, Iterable<V>) -> Pair<K, Iterable<V>>): SetMultimap<K, V> =
    keyMultiValuePairsView()
        .map { mapper(it.one, it.two) }
        .flatMap { pair -> pair.second.map { Pair(pair.first, it) } }
        .toSetMultimap { it }

inline fun <K, V, R> Multimap<K, V>.mapBy(mapper: (K, Iterable<V>) -> R): List<R> =
    keyMultiValuePairsView().map { mapper(it.one, it.two) }
