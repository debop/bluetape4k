package io.bluetape4k.junit5.awaitility

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import org.awaitility.core.ConditionFactory

/**
 * suspend 함수가 true 를 반환할 때까지 대기한다
 *
 * @param block 판단을 위한 코드 블럭
 */
suspend inline infix fun ConditionFactory.awaiting(crossinline block: suspend () -> Unit) =
    untilSuspending { block(); true }

/**
 * suspend 함수가 true 를 반환할 때까지 대기한다
 *
 * @param block 판단을 위한 코드 블럭
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
