package io.bluetape4k.coroutines.support

import io.bluetape4k.coroutines.deferredValueOf
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.trace
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.yield
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeFalse
import org.amshove.kluent.shouldBeGreaterThan
import org.amshove.kluent.shouldBeTrue
import org.junit.jupiter.api.Test

class DeferredExtensionsTest {

    companion object: KLogging() {
        private const val REPEAT_SIZE = 10
    }

    @Test
    fun `값 계산은 async로 시작합니다`() = runTest {
        val x = deferredValueOf {
            log.trace { "Calc deferred value ... " }
            delay(100)
            System.currentTimeMillis()
        }
        val createdTime = System.currentTimeMillis()
        yield()

        x.isActive.shouldBeTrue()
        x.isCompleted.shouldBeFalse()

        // 초기화 진행 후 반환합니다. 이미 초기화가 끝난 후에는 바로 반환합니다.
        x.value shouldBeGreaterThan createdTime

        x.isActive.shouldBeFalse()
        x.isCompleted.shouldBeTrue()
    }

    @Test
    fun `map deferred value`() = runTest {
        val x1 = deferredValueOf {
            log.trace { "Calc deferred value ... " }
            delay(100)
            42
        }
        val x2 = x1.map {
            log.trace { "Map deferred value ... " }
            it * 2
        }

        x1.isCompleted.shouldBeFalse()
        x2.isCompleted.shouldBeFalse()

        x2.await() shouldBeEqualTo 84

        x1.isCompleted.shouldBeTrue()
        x2.isCompleted.shouldBeTrue()
    }

    @Test
    fun `flatmap deferred value`() = runTest {
        val x1 = deferredValueOf {
            log.trace { "Calc deferred value ... " }
            delay(100)

            deferredValueOf { 42 }
        }
        val x2 = x1.flatMap { r ->
            r.map {
                log.trace { "Map deferred value ... " }
                it * 2
            }
        }

        x1.isCompleted.shouldBeFalse()
        x2.isCompleted.shouldBeFalse()

        x2.await() shouldBeEqualTo 84

        x1.isCompleted.shouldBeTrue()
        x2.isCompleted.shouldBeTrue()
    }
}
