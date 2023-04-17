package io.bluetape4k.utils.resilience4j.bulkhead

import io.github.resilience4j.bulkhead.Bulkhead
import io.github.resilience4j.kotlin.bulkhead.executeSuspendFunction

suspend fun <R> withBulkhead(bulkhead: Bulkhead, block: suspend () -> R): R =
    bulkhead.executeSuspendFunction(block)

///**
// * [Bulkhead]를 적용하여 suspend 함수를 수행합니다.
// *
// * @param input 입력 인자
// * @param func  수행할 함수
// */
//suspend fun <T, R> Bulkhead.executeSuspendFunction1(input: T, func: suspend (T) -> R): R {
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
//suspend fun <T, U, R> Bulkhead.executeSuspendFunction2(t: T, u: U, bifunc: suspend (t: T, u: U) -> R): R {
//    acquirePermissionSuspend()
//    return try {
//        bifunc(t, u)
//    } finally {
//        onComplete()
//    }
//}
//
//internal suspend fun Bulkhead.acquirePermissionSuspend() = coroutineScope {
//    // Fast path. Adoid dispatch context switch
//    if (bulkheadConfig.maxWaitDuration.isZero) {
//        acquirePermission()
//    } else {
//        withContext(this.coroutineContext) {
//            acquirePermission()
//        }
//    }
//}
