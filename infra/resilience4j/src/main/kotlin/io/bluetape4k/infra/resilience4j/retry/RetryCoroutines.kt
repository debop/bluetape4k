package io.bluetape4k.infra.resilience4j.retry

import io.github.resilience4j.kotlin.retry.decorateSuspendFunction
import io.github.resilience4j.kotlin.retry.executeSuspendFunction
import io.github.resilience4j.retry.Retry

suspend inline fun <R: Any> withRetry(
    retry: Retry,
    crossinline block: suspend () -> R,
): R {
    return retry.executeSuspendFunction { block() }
}

suspend inline fun <T: Any, R: Any> withRetry(
    retry: Retry,
    input: T,
    crossinline func: suspend (T) -> R,
): R {
    return retry.decorateSuspendFunction1(func).invoke(input)
}

suspend inline fun <T: Any, U: Any, R: Any> withRetry(
    retry: Retry,
    param1: T,
    param2: U,
    crossinline bifunc: suspend (T, U) -> R,
): R {
    return retry.decorateSuspendBiFunction(bifunc).invoke(param1, param2)
}

inline fun <T, R> Retry.decorateSuspendFunction1(
    crossinline func: suspend (input: T) -> R,
): suspend (T) -> R = { input: T ->
    this.decorateSuspendFunction { func(input) }.invoke()
}

inline fun <T, U, R> Retry.decorateSuspendBiFunction(
    crossinline bifunc: suspend (t: T, u: U) -> R,
): suspend (T, U) -> R = { t: T, u: U ->
    this.decorateSuspendFunction { bifunc(t, u) }.invoke()
}
