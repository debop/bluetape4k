package io.bluetape4k.coroutines.flow.extensions.group

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList

fun <T, K> Flow<T>.groupBy(keySelector: suspend (T) -> K): Flow<GroupedFlow<K, T>> =
    FlowGroupBy(this, keySelector) { it }

fun <T, K, V> Flow<T>.groupBy(keySelector: suspend (T) -> K, valueSelector: suspend (T) -> V): Flow<GroupedFlow<K, V>> =
    FlowGroupBy(this, keySelector, valueSelector)

/**
 * [GroupedFlow]의 Value 들만 묶어, `List<V>` 형태의 요소를 제공하는 [Flow]로 변환합니다.
 */
fun <K, V> GroupedFlow<K, V>.asValuesFlow(): Flow<List<V>> {
    val self = this
    return flow {
        val list = self.toList()
        emit(list)
    }
}

/**
 * [GroupedFlow]의 [key] 와 Value 들을 묶어, `Pair<K, List<V>>` 형태의 요소를 제공하는 [Flow]로 변환합니다.
 */
fun <K, V> GroupedFlow<K, V>.asKeyValuesFlow(): Flow<Pair<K, List<V>>> {
    val self = this
    return flow {
        val list = self.toList()
        emit(self.key to list)
    }
}
