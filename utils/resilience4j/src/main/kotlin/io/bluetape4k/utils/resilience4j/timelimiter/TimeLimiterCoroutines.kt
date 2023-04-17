package io.bluetape4k.utils.resilience4j.timelimiter

import io.github.resilience4j.kotlin.timelimiter.executeSuspendFunction
import io.github.resilience4j.timelimiter.TimeLimiter

suspend fun <R> withTimeLimiter(timeLimiter: TimeLimiter, block: suspend () -> R): R =
    timeLimiter.executeSuspendFunction(block)

//suspend fun <T, R> withTimeLimiter(timeLimiter: TimeLimiter, param: T, func: suspend (T) -> R): R =
//    timeLimiter.decorateSuspendFunction1(func).invoke(param)
//
//suspend fun <T, U, R> withTimeLimiter(
//    timeLimiter: TimeLimiter,
//    param1: T,
//    param2: U,
//    bifunc: suspend (T, U) -> R,
//): R =
//    timeLimiter.decorateSuspendFunction2(bifunc).invoke(param1, param2)

//fun <T, R> TimeLimiter.decorateSuspendFunction1(func: suspend (T) -> R): suspend (T) -> R = { input: T ->
//    executeSuspendFunction1(input, func)
//}
//
//fun <T, U, R> TimeLimiter.decorateSuspendFunction2(bifunc: suspend (T, U) -> R): suspend (T, U) -> R = { t: T, u: U ->
//    executeSuspendFunction2(t, u, bifunc)
//}
//
//suspend fun <T, R> TimeLimiter.executeSuspendFunction1(input: T, func: suspend (T) -> R): R {
//    return withTimeout(timeLimiterConfig.timeoutDuration.toMillis()) {
//        func(input)
//    }
//}
//
//suspend fun <T, U, R> TimeLimiter.executeSuspendFunction2(t: T, u: U, bifunc: suspend (T, U) -> R): R {
//    return withTimeout(timeLimiterConfig.timeoutDuration.toMillis()) {
//        bifunc(t, u)
//    }
//}
