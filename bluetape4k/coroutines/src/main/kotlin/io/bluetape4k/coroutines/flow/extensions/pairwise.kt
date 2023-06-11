@file:JvmMultifileClass
@file:JvmName("FlowExtensionsKt")

package io.bluetape4k.coroutines.flow.extensions

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull

/**
 * 요소들의 collect 해서, `Pair((n)-th, (n+1)th)` 를 요소로 emit 하는 Flow 를 빌드합니다.
 *
 * ```
 * val pairs = flowOf(1,2,3,4,5).pairwise().toList()
 * // returns [Pair(1,2), Pair(2,3), Pair(3,4), Pair(4,5)]
 * ```
 */
fun <T> Flow<T>.pairwise(): Flow<Pair<T, T>> {
    return this@pairwise.sliding(2)
        .mapNotNull {
            when (it.size) {
                2    -> Pair(it[0], it[1])
                else -> null
            }
        }
}
