package io.bluetape4k.collections

import java.util.AbstractMap

fun <K, V> Pair<K, V>.toMapEntry(): Map.Entry<K, V> =
    AbstractMap.SimpleEntry(first, second)

fun <K, V> Map.Entry<K, V>.toPair(): Pair<K, V> = this.key to this.value
