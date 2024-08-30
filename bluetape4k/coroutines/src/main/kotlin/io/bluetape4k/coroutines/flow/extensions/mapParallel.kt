@file:JvmMultifileClass
@file:JvmName("FlowExtensionsKt")

package io.bluetape4k.coroutines.flow.extensions

import kotlinx.coroutines.flow.DEFAULT_CONCURRENCY
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * flow의 요소들을 [parallelism]만큼 병렬로 [transform]을 수행하여 처리속도를 높힙니다.
 *
 * @param parallelism 동시 실행할 숫자
 * @param context Coroutine Context
 * @param transform  변환 함수
 */
inline fun <T, R> Flow<T>.mapParallel(
    parallelism: Int = DEFAULT_CONCURRENCY,
    context: CoroutineContext = EmptyCoroutineContext,
    crossinline transform: suspend (value: T) -> R,
): Flow<R> {
    val concurrency = parallelism.coerceAtLeast(2)
    return flatMapMerge(concurrency) { value ->
        flow { emit(transform(value)) }
    }.flowOn(context)
}
