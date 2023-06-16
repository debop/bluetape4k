@file:JvmMultifileClass
@file:JvmName("FlowExtensionsKt")

package io.bluetape4k.coroutines.flow.extensions

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform

/**
 * Emits the given constant value on the output Flow every time the source Flow emits a value.
 *
 * @param value The value to map each source value to.
 */
@Suppress("NOTHING_TO_INLINE")
inline fun <T, R> Flow<T>.mapTo(value: R): Flow<R> = transform { emit(value) }

/**
 * Emits [kotlin.Unit] value on the output Flow every time the source Flow emits a value.
 */
@Suppress("NOTHING_TO_INLINE")
inline fun <T> Flow<T>.mapToUnit(): Flow<Unit> = mapTo(Unit)
