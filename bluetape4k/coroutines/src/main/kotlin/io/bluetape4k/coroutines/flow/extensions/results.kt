@file:JvmMultifileClass
@file:JvmName("FlowExtensionsKt")

package io.bluetape4k.coroutines.flow.extensions

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * [Flow]의 값을 [성공 결과][Result.success]로 매핑하고, 예외를 catch하고 [실패 결과][Result.failure]로 래핑합니다.
 */
fun <T> Flow<T>.mapToResult(): Flow<Result<T>> =
    map { Result.success(it) }
        .catchAndReturn { Result.failure(it) }

/**
 * [Result]의 Flow를 [transform]으로 매핑하여 [Result]의 Flow를 생성합니다.
 *
 * [transform] 함수에서 throw된 예외는 catch되어 결과 플로우로 [실패 결과][Result.failure]로 emit됩니다.
 *
 * @see Result.mapCatching
 */
fun <T, R> Flow<Result<T>>.mapResultCatching(transform: suspend (T) -> R): Flow<Result<R>> =
    map { result ->
        result
            .mapCatching { transform(it) }
            .onFailure {
                if (it is CancellationException) {
                    throw it
                }
            }
    }

/**
 * Maps a [Flow] of [Result]s to a [Flow] of values from successful results.
 * Failure results are re-thrown as exceptions.
 *
 * @see Result.getOrThrow
 */
fun <T> Flow<Result<T>>.throwFailure(): Flow<T> =
    map { it.getOrThrow() }


/**
 * [Result]의 Flow를 매핑하여 [Flow]로 변환합니다. 실패한 결과는 [defaultValue]로 대체됩니다.
 *
 * @see Result.getOrDefault
 */
fun <T> Flow<Result<T>>.getOrDefault(defaultValue: T): Flow<T> =
    map { it.getOrDefault(defaultValue) }
