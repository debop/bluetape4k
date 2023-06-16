@file:JvmMultifileClass
@file:JvmName("FlowExtensionsKt")

package io.bluetape4k.coroutines.flow.extensions

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.flow.flow

inline fun <T, R> Flow<T>.mapIndexed(
    crossinline transform: suspend (index: Int, value: T) -> R,
): Flow<R> = flow {
    this@mapIndexed.collectIndexed { index, value ->
        emit(transform(index, value))
    }
}
