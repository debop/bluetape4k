package io.bluetape4k.infra.resilience4j.retry

import io.github.resilience4j.retry.Retry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

// resilience4j 에서 제공하는 Flow 용 retry 는 flow 를 제공해주는 emitter 의 예외 시에 retry 를 해주는 것이다.
// 여기서는 collect 시에 예외가 발생하는 경우에 retry 를 해주는 것이다.

suspend inline fun <T> Flow<T>.collectWithRetry(
    retry: Retry,
    crossinline collector: suspend (T) -> Unit,
) {
    val decorated = retry.decorateSuspendFunction1(collector)
    onEach { decorated(it) }.collect()
}

inline fun <T, R> Flow<T>.mapWithRetry(
    retry: Retry,
    crossinline mapper: suspend (T) -> R,
): Flow<R> {
    val decorated = retry.decorateSuspendFunction1(mapper)
    return map(decorated)
}

inline fun <T> Flow<T>.onEachWithRetry(
    retry: Retry,
    crossinline consumer: suspend (T) -> Unit,
): Flow<T> {
    val decorated = retry.decorateSuspendFunction1(consumer)
    return onEach(decorated)
}
