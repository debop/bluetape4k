package io.bluetape4k.coroutines

import io.bluetape4k.junit5.random.RandomizedTest
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

@RandomizedTest
class DeferredValueTest {

    companion object: KLogging() {
        private const val REPEAT_SIZE = 10
    }

    @Test
    fun `값 계산은 async로 시작합니다`() = runTest {
        val deferredValue = deferredValueOf {
            log.trace { "Calc deferred value ... " }
            delay(100)
            System.currentTimeMillis()
        }
        val createdTime = System.currentTimeMillis()
        yield()

        deferredValue.isActive.shouldBeTrue()
        deferredValue.isCompleted.shouldBeFalse()

        // 초기화 진행 후 반환합니다. 이미 초기화가 끝난 후에는 바로 반환합니다.
        deferredValue.value shouldBeGreaterThan createdTime

        deferredValue.isActive.shouldBeFalse()
        deferredValue.isCompleted.shouldBeTrue()
    }

    @Test
    fun `map deferred value`() = runTest {
        val deferred1 = deferredValueOf {
            log.trace { "Calc deferred value ... " }
            delay(100)
            42
        }
        val deferred2 = deferred1.map {
            log.trace { "Map deferred value ... " }
            it * 2
        }

        deferred1.isCompleted.shouldBeFalse()
        deferred2.isCompleted.shouldBeFalse()

        deferred2.await() shouldBeEqualTo 42 * 2
        deferred1.await() shouldBeEqualTo 42

        deferred1.isCompleted.shouldBeTrue()
        deferred2.isCompleted.shouldBeTrue()
    }

    @Test
    fun `flatmap deferred value`() = runTest {
        val deferred1 = deferredValueOf {
            log.trace { "Calc deferred value ... " }
            delay(100)

            deferredValueOf { 42 }
        }
        val deferred2 = deferred1.flatMap { r ->
            r.map {
                log.trace { "Map deferred value ... " }
                it * 2
            }
        }

        deferred1.isCompleted.shouldBeFalse()
        deferred2.isCompleted.shouldBeFalse()

        deferred2.await() shouldBeEqualTo 42 * 2

        deferred1.isCompleted.shouldBeTrue()
        deferred2.isCompleted.shouldBeTrue()
    }
}
