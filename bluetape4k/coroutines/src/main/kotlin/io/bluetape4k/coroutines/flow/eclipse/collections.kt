@file:JvmMultifileClass
@file:JvmName("FlowEclipseCollectionKt")

package io.bluetape4k.coroutines.flow.eclipse

import io.bluetape4k.collections.eclipse.fastListOf
import io.bluetape4k.collections.eclipse.unifiedMapOf
import io.bluetape4k.collections.eclipse.unifiedSetOf
import io.bluetape4k.coroutines.flow.extensions.group.GroupedFlow
import io.bluetape4k.coroutines.flow.extensions.group.toGroupItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapMerge
import org.eclipse.collections.api.multimap.Multimap
import org.eclipse.collections.api.multimap.MutableMultimap
import org.eclipse.collections.impl.factory.Multimaps
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

suspend fun <K, V> Flow<GroupedFlow<K, V>>.toMultiMap(
    destination: MutableMultimap<K, V> = Multimaps.mutable.list.of(),
): Multimap<K, V> {
    this.flatMapMerge { it.toGroupItem() }
        .collect { groupItem ->
            destination.putAll(groupItem.key, groupItem.values)
        }
    return destination
}
