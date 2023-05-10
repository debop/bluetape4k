package io.bluetape4k.infra.resilience4j.bulkhead

import io.github.resilience4j.bulkhead.Bulkhead
import io.github.resilience4j.kotlin.bulkhead.decorateSuspendFunction
import io.github.resilience4j.kotlin.bulkhead.executeSuspendFunction

suspend inline fun <R> withBulkhead(
    bulkhead: Bulkhead,
    crossinline block: suspend () -> R,
): R {
    return bulkhead.executeSuspendFunction { block() }
}

suspend inline fun <T, R> withBulkhead(
    bulkhead: Bulkhead,
    param: T,
    crossinline func: suspend (T) -> R,
): R {
    return bulkhead.decorateSuspendFunction1(func).invoke(param)
}

suspend inline fun <T, U, R> withBulkhead(
    bulkhead: Bulkhead,
    param1: T,
    param2: U,
    crossinline bifunc: suspend (T, U) -> R,
): R {
    return bulkhead.decorateSuspendBiFunction(bifunc).invoke(param1, param2)
}

/**
 * suspend [func] 실행에 실패하는 경우, Resilience4j의 Bulkhead 를 이용하여, 실행을 제어합니다.
 *
 * @param func Bulkhead 로 decorate 할 suspend 함수
 * @return retry로 decorated 된 suspend function
 */
inline fun <T, R> Bulkhead.decorateSuspendFunction1(
    crossinline func: suspend (input: T) -> R,
): suspend (T) -> R = { input: T ->
    this.decorateSuspendFunction { func(input) }.invoke()
}

/**
 * suspend [func] 실행에 실패하는 경우, Resilience4j의 Bulkhead 를 이용하여, 실행을 제어합니다.
 *
 * @param func Bulkhead 로 decorate 할 suspend 함수
 * @return retry로 decorated 된 suspend function
 */
inline fun <T1, T2, R> Bulkhead.decorateSuspendBiFunction(
    crossinline func: suspend (input1: T1, input2: T2) -> R,
): suspend (T1, T2) -> R = { input1: T1, input2: T2 ->
    this.decorateSuspendFunction { func(input1, input2) }.invoke()
}
//
///**
// * [Bulkhead]를 적용하여 suspend 함수를 수행합니다.
// *
// * @param input 입력 인자
// * @param func  수행할 함수
// */
//suspend inline fun <T, R> Bulkhead.executeSuspendFunction1(
//    input: T,
//    crossinline func: suspend (T) -> R,
//): R {
//    acquirePermissionSuspend()
//    return try {
//        func(input)
//    } finally {
//        onComplete()
//    }
//}
//
///**
// * [Bulkhead]를 적용하여 suspend 함수를 수행합니다.
// *
// * @param t      첫번째 입력값
// * @param u      두번째 입력값
// * @param bifunc 수행할 함수
// */
//suspend inline fun <T, U, R> Bulkhead.executeSuspendBiFunction(
//    t: T,
//    u: U,
//    crossinline bifunc: suspend (t: T, u: U) -> R,
//): R {
//    acquirePermissionSuspend()
//    return try {
//        bifunc(t, u)
//    } finally {
//        onComplete()
//    }
//}
//
//suspend fun Bulkhead.acquirePermissionSuspend() = coroutineScope {
//    // Fast path. Adoid dispatch context switch
//    if (bulkheadConfig.maxWaitDuration.isZero) {
//        acquirePermission()
//    } else {
//        withContext(this.coroutineContext) {
//            acquirePermission()
//        }
//    }
//}
