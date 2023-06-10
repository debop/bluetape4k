@file:JvmMultifileClass
@file:JvmName("FlowExtensionsKt")

package io.bluetape4k.coroutines.flow.extensions

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.DEFAULT_CONCURRENCY
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlin.coroutines.CoroutineContext

/**
 * flow의 요소들을 [concurrency]만큼 병렬로 [transform]을 수행하여 처리속도를 높힙니다.
 *
 * @param coroutineContext Coroutine context
 * @param concurrency 동시 실행할 숫자
 * @param transform  변환 함수
 */
inline fun <T, R> Flow<T>.mapParallel(
    coroutineContext: CoroutineContext = Dispatchers.Default,
    concurrency: Int = DEFAULT_CONCURRENCY,
    crossinline transform: suspend (value: T) -> R,
): Flow<R> =
    flatMapMerge(concurrency.coerceAtLeast(2)) { value ->
        flow { emit(transform(value)) }.flowOn(coroutineContext)
    }
