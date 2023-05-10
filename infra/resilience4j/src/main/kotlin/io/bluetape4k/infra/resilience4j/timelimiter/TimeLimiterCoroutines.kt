package io.bluetape4k.infra.resilience4j.timelimiter

import io.github.resilience4j.kotlin.timelimiter.decorateSuspendFunction
import io.github.resilience4j.kotlin.timelimiter.executeSuspendFunction
import io.github.resilience4j.timelimiter.TimeLimiter

suspend fun <R> withTimeLimiter(
    timeLimiter: TimeLimiter,
    block: suspend () -> R,
): R {
    return timeLimiter.executeSuspendFunction(block)
}

suspend inline fun <T, R> withTimeLimiter(
    timeLimiter: TimeLimiter,
    param: T,
    crossinline func: suspend (T) -> R,
): R {
    return timeLimiter.decorateSuspendFunction1(func).invoke(param)
}

suspend inline fun <T, U, R> withTimeLimiter(
    timeLimiter: TimeLimiter,
    param1: T,
    param2: U,
    crossinline bifunc: suspend (T, U) -> R,
): R {
    return timeLimiter.decorateSuspendBiFunction(bifunc).invoke(param1, param2)
}

inline fun <T, R> TimeLimiter.decorateSuspendFunction1(
    crossinline func: suspend (T) -> R,
): suspend (T) -> R = { input: T ->
    decorateSuspendFunction { func(input) }.invoke()
}

inline fun <T, U, R> TimeLimiter.decorateSuspendBiFunction(
    crossinline bifunc: suspend (T, U) -> R,
): suspend (T, U) -> R = { t: T, u: U ->
    decorateSuspendFunction { bifunc(t, u) }.invoke()
}

//suspend inline fun <T, R> TimeLimiter.executeSuspendFunction1(
//    input: T,
//    crossinline func: suspend (T) -> R,
//): R {
//    val timeoutMillis = timeLimiterConfig.timeoutDuration.toMillis()
//    return withTimeout(timeoutMillis) {
//        func(input)
//    }
//}
//
//suspend inline fun <T, U, R> TimeLimiter.executeSuspendBiFunction(
//    t: T,
//    u: U,
//    crossinline bifunc: suspend (T, U) -> R,
//): R {
//    val timeoutMillis = timeLimiterConfig.timeoutDuration.toMillis()
//    return withTimeout(timeoutMillis) {
//        bifunc(t, u)
//    }
//}
