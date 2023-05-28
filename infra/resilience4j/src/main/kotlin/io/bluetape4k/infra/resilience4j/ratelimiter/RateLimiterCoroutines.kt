package io.bluetape4k.infra.resilience4j.ratelimiter

import io.github.resilience4j.kotlin.ratelimiter.decorateSuspendFunction
import io.github.resilience4j.kotlin.ratelimiter.executeSuspendFunction
import io.github.resilience4j.ratelimiter.RateLimiter

suspend fun <R> withRateLimiter(
    rateLimiter: RateLimiter,
    block: suspend () -> R,
): R {
    return rateLimiter.executeSuspendFunction(block)
}

suspend fun <T, R> withRateLimiter(
    rateLimiter: RateLimiter,
    param: T,
    func: suspend (T) -> R,
): R {
    return rateLimiter.decorateSuspendFunction1(func).invoke(param)
}

suspend fun <T, U, R> withRateLimiter(
    rateLimiter: RateLimiter,
    param1: T,
    param2: U,
    bifunc: suspend (T, U) -> R,
): R {
    return rateLimiter.decorateSuspendBiFunction(bifunc).invoke(param1, param2)
}

inline fun <T, R> RateLimiter.decorateSuspendFunction1(
    crossinline func: suspend (T) -> R,
): suspend (T) -> R = { input ->
    decorateSuspendFunction { func(input) }.invoke()
    // executeSuspendFunction1(input, func)
}

fun <T, U, R> RateLimiter.decorateSuspendBiFunction(
    bifunc: suspend (T, U) -> R,
): suspend (T, U) -> R = { t: T, u: U ->
    decorateSuspendFunction { bifunc(t, u) }.invoke()
}
