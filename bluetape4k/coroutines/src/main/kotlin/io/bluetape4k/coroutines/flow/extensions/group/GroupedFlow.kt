package io.bluetape4k.coroutines.flow.extensions.group

import kotlinx.coroutines.flow.Flow

/**
 * Key 로 Grouping 된 [Flow] 를 표현하는 interface 입니다.
 */
interface GroupedFlow<K, V>: Flow<V> {

    /**
     * The grouped key of the flow
     */
    val key: K
}
