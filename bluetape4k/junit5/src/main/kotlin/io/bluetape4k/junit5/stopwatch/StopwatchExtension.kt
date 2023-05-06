package io.bluetape4k.junit5.stopwatch

import io.bluetape4k.junit5.store
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.info
import org.junit.jupiter.api.extension.AfterTestExecutionCallback
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.slf4j.Logger
import java.time.Duration

/**
 * Stopwatch 를 이용하여 테스트 실행 시간을 측정하는 JUnit5 Extension 입니다.
 *
 * ```
 * @StopWatcherTest
 * class TestClass {
 *    ....
 * }
 * ```
 */
class StopwatchExtension(val logger: Logger = log): BeforeTestExecutionCallback, AfterTestExecutionCallback {

    companion object: KLogging()

    override fun beforeTestExecution(context: ExtensionContext) {
        val testMethod = context.requiredTestMethod
        logger.info { "Starting test [${testMethod.name}]" }
        context.store(this.javaClass).put(testMethod, System.nanoTime())
    }

    override fun afterTestExecution(context: ExtensionContext) {
        val testMethod = context.requiredTestMethod
        val startNano = context.store(this.javaClass).get(testMethod, Long::class.java)
        val duration = Duration.ofNanos(System.nanoTime() - startNano)

        val millis = duration.toMillis()
        if (millis == 0L) {
            logger.info { "Completed test [${testMethod.name}] took $duration" }
        } else {
            logger.info { "Completed test [${testMethod.name}] took $millis msecs." }
        }
    }
}
