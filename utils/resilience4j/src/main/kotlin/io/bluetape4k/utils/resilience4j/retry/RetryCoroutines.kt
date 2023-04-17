package io.bluetape4k.utils.resilience4j.retry

import io.github.resilience4j.kotlin.retry.executeSuspendFunction
import io.github.resilience4j.retry.Retry


suspend fun <R: Any> withRetry(retry: Retry, block: suspend () -> R): R =
    retry.executeSuspendFunction(block)

//suspend fun <T: Any, R: Any> withRetry(retry: Retry, input: T, func: suspend (T) -> R): R =
//    retry.decorateSuspendFunction1(func).invoke(input)
//
//suspend fun <T: Any, U: Any, R: Any> withRetry(retry: Retry, param1: T, param2: U, bifunc: suspend (T, U) -> R): R =
//    retry.decorateSuspendFunction2(bifunc).invoke(param1, param2)
//
//fun <T: Any, R: Any> Retry.decorateSuspendFunction1(func: suspend (input: T) -> R): suspend (T) -> R = {
//    executeSuspendFunction1(it, func)
//}
//
//fun <T: Any, U: Any, R: Any> Retry.decorateSuspendFunction2(bifunc: suspend (t: T, u: U) -> R): suspend (T, U) -> R =
//    { t: T, u: U ->
//        executeSuspendFunction2(t, u, bifunc)
//    }
//
///**
// * suspended function 수행 시 [Retry]를 적용해서 수행할 수 있도록 합니다.
// *
// * @param func 실행할 Suspended 함수
// * @return Retry로 decorate 된 suspend 함수
// */
//suspend fun <T: Any, R: Any> Retry.executeSuspendFunction1(
//    input: T,
//    func: suspend (input: T) -> R,
//): R {
//    val retryContext = asyncContext<R>()
//    var result: R
//
//    while (true) {
//        try {
//            result = func(input)
//            val delayMs = retryContext.onResult(result)
//            if (delayMs < 1) {
//                retryContext.onResult(result)
//                break
//            } else {
//                delay(delayMs)
//            }
//        } catch (e: Exception) {
//            val delayMs = retryContext.onError(e)
//            if (delayMs >= 0) {
//                delay(delayMs)
//            } else {
//                throw e
//            }
//        }
//    }
//    return result
//}
//
///**
// * suspended bi-function 수행 시 [Retry]를 적용해서 수행할 수 있도록 합니다.
// *
// * @param bifunc 실행할 함수
// * @return Retry로 decorate 된 suspended bifunction
// */
//suspend fun <T: Any, U: Any, R: Any> Retry.executeSuspendFunction2(
//    t: T,
//    u: U,
//    bifunc: suspend (t: T, u: U) -> R,
//): R {
//
//    val retryContext = asyncContext<R>()
//    var result: R
//
//    while (true) {
//        try {
//            result = bifunc(t, u)
//            val delayMs = retryContext.onResult(result)
//            if (delayMs < 1) {
//                retryContext.onResult(result)
//                break
//            } else {
//                delay(delayMs)
//            }
//        } catch (e: Exception) {
//            val delayMs = retryContext.onError(e)
//            if (delayMs >= 0) {
//                delay(delayMs)
//            } else {
//                throw e
//            }
//        }
//    }
//    return result
//}
