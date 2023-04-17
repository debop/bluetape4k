package io.bluetape4k.utils.resilience4j.ratelimiter

import io.github.resilience4j.kotlin.ratelimiter.executeSuspendFunction
import io.github.resilience4j.ratelimiter.RateLimiter

suspend fun <R> withRateLimiter(rateLimiter: RateLimiter, block: suspend () -> R): R =
    rateLimiter.executeSuspendFunction(block)

//suspend fun <T, R> withRateLimiter(rateLimiter: RateLimiter, param: T, func: suspend (T) -> R): R =
//    rateLimiter.decorateSuspendFunction1(func).invoke(param)
//
//suspend fun <T, U, R> withRateLimiter(
//    rateLimiter: RateLimiter,
//    param1: T,
//    param2: U,
//    bifunc: suspend (T, U) -> R,
//): R =
//    rateLimiter.decorateSuspendFunction2(bifunc).invoke(param1, param2)


//fun <T, R> RateLimiter.decorateSuspendFunction1(func: suspend (T) -> R): suspend (T) -> R = { input ->
//    executeSuspendFunction1(input, func)
//}
//
//fun <T, U, R> RateLimiter.decorateSuspendFunction2(bifunc: suspend (T, U) -> R): suspend (T, U) -> R = { t: T, u: U ->
//    executeSuspendFunction2(t, u, bifunc)
//}
//
///**
// * [RateLimit]을 적용하여 suspend function 수행합니다.
// *
// * @param func 실행할 function body
// * @return RateLimiter로 decorate된 suspended function
// */
//suspend fun <T, R> RateLimiter.executeSuspendFunction1(input: T, func: suspend (T) -> R): R {
//    awaitPermission()
//    return func(input)
//}
//
///**
// * [RateLimit]을 적용하여 suspend function 수행합니다.
// *
// * @param bifunc 실행할 function body
// * @return RateLimiter로 decorate된 suspended function
// */
//suspend fun <T, U, R> RateLimiter.executeSuspendFunction2(t: T, u: U, bifunc: suspend (T, U) -> R): R {
//    awaitPermission()
//    return bifunc(t, u)
//}

//internal suspend fun RateLimiter.awaitPermission() {
//    val waitTimeNs = reservePermission()
//    when {
//        waitTimeNs > 0 -> delay(TimeUnit.NANOSECONDS.toMillis(waitTimeNs))
//        waitTimeNs < 0 -> throw RequestNotPermitted.createRequestNotPermitted(this)
//    }
//}
