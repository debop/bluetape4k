package io.bluetape4k.infra.resilience4j.circuitbreaker

import io.github.resilience4j.circuitbreaker.CircuitBreaker
import io.github.resilience4j.kotlin.circuitbreaker.executeSuspendFunction

suspend fun <R> withCircuitBreaker(circuitBreaker: CircuitBreaker, block: suspend () -> R): R =
    circuitBreaker.executeSuspendFunction(block)

//suspend fun <T, R> withCircuitBreaker(
//    circuitBreaker: CircuitBreaker,
//    param: T,
//    func: suspend (T) -> R,
//): R =
//    circuitBreaker.decorateSuspendFunction1(func).invoke(param)
//
//suspend fun <T, U, R> withCircuitBreaker(
//    circuitBreaker: CircuitBreaker,
//    param1: T,
//    param2: U,
//    bifunc: suspend (T, U) -> R,
//): R =
//    circuitBreaker.decorateSuspendFunction2(bifunc).invoke(param1, param2)
//
//
//fun <T, R> CircuitBreaker.decorateSuspendFunction1(func: suspend (T) -> R): suspend (T) -> R = { input: T ->
//    executeSuspendFunction1(input, func)
//}
//
//fun <T, U, R> CircuitBreaker.decorateSuspendFunction2(bifunc: suspend (T, U) -> R): suspend (T, U) -> R = { t: T, u: U ->
//    executeSuspendFunction2(t, u, bifunc)
//}
//
//
//suspend fun <T, R> CircuitBreaker.executeSuspendFunction1(input: T, func: suspend (T) -> R): R {
//    acquirePermission()
//
//    val start = System.nanoTime()
//    try {
//        val result = func(input)
//        onSuccess(System.nanoTime() - start, TimeUnit.NANOSECONDS)
//        return result
//    } catch (e: Exception) {
//        onError(System.nanoTime() - start, TimeUnit.NANOSECONDS, e)
//        throw e
//    }
//}
//
//suspend fun <T, U, R> CircuitBreaker.executeSuspendFunction2(t: T, u: U, bifunc: suspend (T, U) -> R): R {
//    acquirePermission()
//
//    val start = System.nanoTime()
//    try {
//        val result = bifunc(t, u)
//        onSuccess(System.nanoTime() - start, TimeUnit.NANOSECONDS)
//        return result
//    } catch (e: Exception) {
//        onError(System.nanoTime() - start, TimeUnit.NANOSECONDS, e)
//        throw e
//    }
//}
