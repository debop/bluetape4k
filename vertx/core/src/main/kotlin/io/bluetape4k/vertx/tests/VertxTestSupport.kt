package io.bluetape4k.vertx.tests

import io.vertx.core.Vertx
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking

/**
 * Vertx Framework 테스트 시 [VertxTestContext]를 사용하여 테스트를 수행합니다.
 *
 * @param testContext [VertxTestContext] 인스턴스
 * @param block 실행할 테스트 코드 블럭
 */
inline fun withTestContext(testContext: VertxTestContext, block: () -> Unit) {
    try {
        block()
        testContext.completeNow()
    } catch (e: Throwable) {
        testContext.failNow(e)
    }
}

/**
 * Vertx Framework 테스트 시 [VertxTestContext]를 사용하여 Coroutines 환경에서 테스트를 수행합니다.
 *
 * @param vertx       [Vertx] 인스턴스
 * @param testContext [VertxTestContext] 인스턴스
 * @param block 실행할 Coroutines 테스트 코드 블럭
 */
inline fun Vertx.withTestContextSuspending(
    testContext: VertxTestContext,
    crossinline block: suspend CoroutineScope.() -> Unit,
) {
    runBlocking(dispatcher()) {
        try {
            block()
            testContext.completeNow()
        } catch (e: Throwable) {
            testContext.failNow(e)
        }
    }
}
