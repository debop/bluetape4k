package io.bluetape4k.junit5.awaitility

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import org.awaitility.core.ConditionFactory

/**
 * suspend 함수가 true 를 반환할 때까지 대기한다
 *
 * @param condition 판단을 위한 코드 블럭
 */
suspend inline infix fun ConditionFactory.await(crossinline block: suspend () -> Unit) =
    untilSuspending { block(); true }

/**
 * suspend 함수가 true 를 반환할 때까지 대기한다
 *
 * @param condition 판단을 위한 코드 블럭
 */
suspend inline infix fun ConditionFactory.untilSuspending(
    crossinline block: suspend () -> Boolean,
) = coroutineScope {
    while (isActive) {
        if (block()) {
            break
        }
        delay(10)
    }
}

/**
 * suspend 함수가 true 를 반환할 때까지 대기한다
 *
 * @param supplier [predicate]가 판단할 수 있는 값을 제공합니다
 * @param predicate 대기 판단을 수행합니다
 */
@Deprecated("use untilSuspend(block)")
@Suppress("UNCHECKED_CAST")
suspend inline fun <T> ConditionFactory.untilSuspending(
    crossinline block: suspend () -> T,
    crossinline predicate: (T) -> Boolean,
): T {
    return coroutineScope {
        while (isActive) {
            val result = block()
            if (predicate(result)) {
                return@coroutineScope result
            }
            delay(10)
        }
        null as T
    }
}
