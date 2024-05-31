@file:JvmMultifileClass
@file:JvmName("FlowExtensionsKt")

package io.bluetape4k.coroutines.flow.extensions

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform

/**
 * source가 방출된 값 대신 지정한 [value]를 emit 하는 [Flow] 로 변환합니다.
 *
 * @param value source의 emit 값 대신 emit 할 값
 */
@Suppress("NOTHING_TO_INLINE")
inline fun <T, R> Flow<T>.mapTo(value: R): Flow<R> = transform { emit(value) }

/**
 * emit 되는 값 대신 [kotlin.Unit] 을 emit 합니다.
 */
@Suppress("NOTHING_TO_INLINE")
inline fun <T> Flow<T>.mapToUnit(): Flow<Unit> = mapTo(Unit)
