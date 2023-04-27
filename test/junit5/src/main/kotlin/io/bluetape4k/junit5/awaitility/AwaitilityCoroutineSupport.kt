package io.bluetape4k.junit5.awaitility

import kotlinx.coroutines.runBlocking
import org.awaitility.core.ConditionFactory

/**
 * suspend 함수가 true 를 반환할 때까지 대기한다
 *
 * @param condition 판단을 위한 코드 블럭
 */
infix fun ConditionFactory.await(block: suspend () -> Unit) {
    until { runBlocking { block() }; true }
}

/**
 * suspend 함수가 true 를 반환할 때까지 대기한다
 *
 * @param condition 판단을 위한 코드 블럭
 */
infix fun ConditionFactory.untilSuspending(block: suspend () -> Boolean) {
    until { runBlocking { block() } }
}

/**
 * suspend 함수가 true 를 반환할 때까지 대기한다
 *
 * @param supplier [predicate]가 판단할 수 있는 값을 제공합니다
 * @param predicate 대기 판단을 수행합니다
 */
fun <T> ConditionFactory.untilSuspending(block: suspend () -> T, predicate: (T) -> Boolean): T {
    return until({ runBlocking { block() } }, { predicate(it) })
}
