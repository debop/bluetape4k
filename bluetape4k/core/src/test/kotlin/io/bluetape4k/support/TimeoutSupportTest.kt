package io.bluetape4k.support

import io.bluetape4k.concurrent.FutureUtils
import io.bluetape4k.logging.KLogging
import kotlinx.coroutines.future.await
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeInstanceOf
import org.junit.jupiter.api.RepeatedTest
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import kotlin.test.assertFailsWith

class TimeoutSupportTest {

    companion object: KLogging() {
        private const val REPEAT_SIZE = 5
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `제한시간이 적용된 비동기 작업을 수행한다`() {

        val future = asyncRunWithTimeout(1000) {
            Thread.sleep(100)
        }

        future.get() // 완료되어야 함
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `제한시간이 적용된 복수의 비동기 작업을 수행`() {
        val futures = List(1000) {
            asyncRunWithTimeout(1000) {
                Thread.sleep(100)
            }
        }

        FutureUtils.allAsList(futures)
            .orTimeout(1000, TimeUnit.MILLISECONDS)
            .get() // 모두 완료되어야 함
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `제한시간을 초과하는 비동기 작업은 예외를 발생시킨다`() {
        assertFailsWith<ExecutionException> {
            asyncRunWithTimeout(500) {
                Thread.sleep(1000)
            }.get()
        }.cause shouldBeInstanceOf TimeoutException::class
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `제한시간을 초과하는 비동기 작업에 대한 Non-Blocking 방법`() = runTest {
        assertFailsWith<TimeoutException> {
            val future = asyncRunWithTimeout(500) {
                Thread.sleep(1000)
            }
            future.await()
        }
    }
}
