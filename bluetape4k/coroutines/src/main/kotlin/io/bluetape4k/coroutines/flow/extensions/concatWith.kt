@file:JvmMultifileClass
@file:JvmName("FlowExtensionsKt")

package io.bluetape4k.coroutines.flow.extensions

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.onCompletion

/**
 * Current flow 를 모두 collect 하고난 후, [other] 를 collect 합니다.
 *
 * @param T
 * @param other
 * @return
 */
fun <T> Flow<T>.concatWithEx(other: Flow<T>): Flow<T> {
    val source = this
    return source.onCompletion { error -> if (error == null) emitAll(other) }
}
