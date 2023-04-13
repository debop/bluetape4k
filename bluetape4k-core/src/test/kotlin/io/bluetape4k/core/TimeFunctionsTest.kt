package io.bluetape4k.core

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.trace
import kotlinx.atomicfu.atomic
import org.amshove.kluent.internal.assertFailsWith
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeFalse
import org.amshove.kluent.shouldBeNull
import org.amshove.kluent.shouldBeTrue
import org.awaitility.kotlin.await
import org.awaitility.kotlin.until
import org.junit.jupiter.api.Test
import java.util.concurrent.ExecutionException

class TimeFunctionsTest {

    companion object : KLogging()

    //    @Suppress("UNREACHABLE_CODE")
    @Test
    fun `함수 실행이 timeout에 걸릴때는 예외를 발생시키는 CompletableFuture 반환한다`() {
        val isWorking = atomic(false)
        val future = asyncWithTimeout(500) {
            var i = 0
            isWorking.value = true
            while (true) {
                Thread.sleep(100)
                log.trace { "Working... $i" }
                i++
            }
            //            isWorking.value = false
            //            "Hello"
        }
        Thread.sleep(600)
        future.isDone.shouldBeTrue()
        future.isCompletedExceptionally.shouldBeTrue()

        assertFailsWith<ExecutionException> {
            future.get()
        }
        log.trace { "작업 종료: ${isWorking.value}" }
    }

    @Test
    fun `함수 실행이 timeout 에 걸리지 않으면 작업 결과를 반환한다`() {
        val isWorking = atomic(false)
        val future = asyncWithTimeout(500) {
            isWorking.value = true
            var i = 0
            while (i < 2) {
                Thread.sleep(100)
                log.trace { "Working... $i" }
                i++
            }
            isWorking.value = false
            "Hello"
        }

        await until { !isWorking.value }

        future.get() shouldBeEqualTo "Hello"
        log.trace { "작업 종료: ${isWorking.value}" }
    }

    @Test
    fun `함수 실행이 timeout에 걸릴때는 null을 반환한다`() {
        val isWorking = atomic(false)
        val result = withTimeoutOrNull(500) {
            var i = 0
            isWorking.value = true
            while (true) {
                Thread.sleep(100)
                log.trace { "Working... $i" }
                i++
            }
            isWorking.value = false
            "Hello"
        }
        result.shouldBeNull()
        log.trace { "작업 종료: ${isWorking.value}" }
        // 함수 실행이 계속되는지 확인
        Thread.sleep(100)
    }

    @Test
    fun `함수 실행이 timeout 보다 빨리 끝나면 함수 실행 반환값을 반환한다`() {
        val isWorking = atomic(false)
        val result = withTimeoutOrNull(500) {
            isWorking.value = true
            var i = 0
            while (i < 2) {
                Thread.sleep(100)
                log.trace { "Working... $i" }
                i++
            }
            isWorking.value = false
            "Hello"
        }

        result shouldBeEqualTo "Hello"
        log.trace { "작업 종료: ${isWorking.value}" }
        isWorking.value.shouldBeFalse()
    }
}