package io.bluetape4k.collections.eclipse

import org.eclipse.collections.impl.tuple.Tuples
import java.util.*
import kotlin.Pair
import org.eclipse.collections.api.tuple.Pair as EcPair

fun <K, V> Pair<K, V>.toTuplePair(): EcPair<K, V> = Tuples.pair(first, second)

fun <K, V> EcPair<K, V>.toPair(): Pair<K, V> = one to two

fun <K, V> Map.Entry<K, V>.toEcPair(): EcPair<K, V> = Tuples.pair(key, value)

fun <T1, T2> EcPair<T1, T2>.toMapEntry(): Map.Entry<T1, T2> = AbstractMap.SimpleEntry(one, two)
