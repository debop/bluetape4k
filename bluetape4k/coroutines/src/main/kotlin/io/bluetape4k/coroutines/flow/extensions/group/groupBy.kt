package io.bluetape4k.coroutines.flow.extensions.group

import io.bluetape4k.coroutines.flow.extensions.flowFromSuspend
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.toList
import java.io.Serializable

data class GroupItem<K, V>(
    val key: K,
    val values: List<V>,
): Serializable


fun <T, K> Flow<T>.groupBy(keySelector: (T) -> K): Flow<GroupedFlow<K, T>> =
    FlowGroupBy(this, keySelector) { it }

fun <T, K, V> Flow<T>.groupBy(keySelector: (T) -> K, valueSelector: (T) -> V): Flow<GroupedFlow<K, V>> =
    FlowGroupBy(this, keySelector, valueSelector)

/**
 * [GroupedFlow]의 Value들만 묶어, `List<V>` 형태의 요소를 제공하는 [Flow]로 변환합니다.
 */
fun <K, V> GroupedFlow<K, V>.toValues(): Flow<List<V>> = flowFromSuspend { toList() }

/**
 * [GroupedFlow]의 [key] 와 Value들을 묶어, `Pair<K, List<V>>` 형태의 요소를 제공하는 [Flow]로 변환합니다.
 */
fun <K, V> GroupedFlow<K, V>.toGroupItem(): Flow<GroupItem<K, V>> = flowFromSuspend {
    val values = toList()
    GroupItem(key, values)
}

suspend fun <K, V> Flow<GroupedFlow<K, V>>.toMap(destination: MutableMap<K, List<V>> = mutableMapOf()): MutableMap<K, List<V>> {
    this.flatMapMerge { it.toGroupItem() }
        .collect { groupItem ->
            destination[groupItem.key] = groupItem.values
        }
    return destination
}
