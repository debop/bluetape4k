package io.bluetape4k.coroutines.flow.eclipse

import io.bluetape4k.collections.eclipse.fastListOf
import io.bluetape4k.collections.eclipse.primitives.asSequence
import io.bluetape4k.collections.eclipse.unifiedMapOf
import io.bluetape4k.collections.eclipse.unifiedSetOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import org.eclipse.collections.api.CharIterable
import org.eclipse.collections.api.DoubleIterable
import org.eclipse.collections.api.FloatIterable
import org.eclipse.collections.api.IntIterable
import org.eclipse.collections.api.LongIterable
import org.eclipse.collections.api.RichIterable
import org.eclipse.collections.api.ShortIterable
import org.eclipse.collections.impl.list.mutable.FastList
import org.eclipse.collections.impl.map.mutable.UnifiedMap
import org.eclipse.collections.impl.set.mutable.UnifiedSet

suspend fun <T> Flow<T>.toFastList(destination: FastList<T> = fastListOf()): FastList<T> {
    collect { value -> destination.add(value) }
    return destination
}

suspend fun <T> Flow<T>.toUnifiedSet(destination: UnifiedSet<T> = unifiedSetOf()): UnifiedSet<T> {
    collect { value -> destination.add(value) }
    return destination
}

suspend fun <T, K> Flow<T>.toUnifiedMap(
    destination: UnifiedMap<K, T> = unifiedMapOf(),
    keySelector: (T) -> K,
): UnifiedMap<K, T> {
    collect { value: T -> destination[keySelector(value)] = value }
    return destination
}

fun <T> RichIterable<T>.asFlow(): Flow<T> = asSequence().asFlow()

fun CharIterable.asFlow(): Flow<Char> = asSequence().asFlow()
fun ShortIterable.asFlow(): Flow<Short> = asSequence().asFlow()
fun IntIterable.asFlow(): Flow<Int> = asSequence().asFlow()
fun LongIterable.asFlow(): Flow<Long> = asSequence().asFlow()
fun FloatIterable.asFlow(): Flow<Float> = asSequence().asFlow()
fun DoubleIterable.asFlow(): Flow<Double> = asSequence().asFlow()
