package io.bluetape4k.infra.resilience4j.circuitbreaker

import io.github.resilience4j.circuitbreaker.CircuitBreaker
import io.github.resilience4j.kotlin.circuitbreaker.decorateSuspendFunction
import io.github.resilience4j.kotlin.circuitbreaker.executeSuspendFunction

suspend inline fun <R> withCircuitBreaker(
    circuitBreaker: CircuitBreaker,
    crossinline block: suspend () -> R,
): R {
    return circuitBreaker.executeSuspendFunction { block() }
}

suspend inline fun <T, R> withCircuitBreaker(
    circuitBreaker: CircuitBreaker,
    param: T,
    crossinline func: suspend (T) -> R,
): R {
    return circuitBreaker.decorateSuspendFunction1(func).invoke(param)
}

suspend inline fun <T, U, R> withCircuitBreaker(
    circuitBreaker: CircuitBreaker,
    param1: T,
    param2: U,
    crossinline bifunc: suspend (T, U) -> R,
): R {
    return circuitBreaker.decorateSuspendBiFunction(bifunc).invoke(param1, param2)
}

inline fun <T, R> CircuitBreaker.decorateSuspendFunction1(
    crossinline func: suspend (T) -> R,
): suspend (T) -> R = { input: T ->
    decorateSuspendFunction { func(input) }.invoke()
}

inline fun <T, U, R> CircuitBreaker.decorateSuspendBiFunction(
    crossinline bifunc: suspend (T, U) -> R,
): suspend (T, U) -> R = { t: T, u: U ->
    decorateSuspendFunction { bifunc(t, u) }.invoke()
}

//
//suspend inline fun <T, R> CircuitBreaker.executeSuspendFunction1(
//    input: T,
//    crossinline func: suspend (T) -> R,
//): R {
//    acquirePermission()
//
//    val start = System.nanoTime()
//    try {
//        val result = func(input)
//        onSuccess(System.nanoTime() - start, TimeUnit.NANOSECONDS)
//        return result
//    } catch (e: Throwable) {
//        onError(System.nanoTime() - start, TimeUnit.NANOSECONDS, e)
//        throw e
//    }
//}
//
//suspend inline fun <T, U, R> CircuitBreaker.executeSuspendBiFunction(
//    t: T,
//    u: U,
//    crossinline bifunc: suspend (T, U) -> R,
//): R {
//    acquirePermission()
//
//    val start = System.nanoTime()
//    try {
//        val result = bifunc(t, u)
//        onSuccess(System.nanoTime() - start, TimeUnit.NANOSECONDS)
//        return result
//    } catch (e: Throwable) {
//        onError(System.nanoTime() - start, TimeUnit.NANOSECONDS, e)
//        throw e
//    }
//}
