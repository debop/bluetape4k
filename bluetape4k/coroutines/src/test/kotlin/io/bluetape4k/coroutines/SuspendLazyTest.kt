package io.bluetape4k.coroutines

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.yield
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.RepeatedTest
import kotlin.random.Random

class SuspendLazyTest {

    companion object: KLogging() {
        private const val REPEAT_SIZE = 5
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `get suspend lazy value in coroutine scope`() = runTest {
        val calledCounter = atomic(0)
        val called by calledCounter

        val lazyValue = suspendLazy {
            delay(Random.nextLong(100))
            log.debug { "Calculate lazy value in suspend function." }
            calledCounter.incrementAndGet()
            42
        }
        called shouldBeEqualTo 0

        yield()

        lazyValue() shouldBeEqualTo 42
        lazyValue() shouldBeEqualTo 42

        called shouldBeEqualTo 1
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `get lazy value in blocking mode`() = runTest {
        val calledCounter = atomic(0)
        val called by calledCounter

        val lazyValue = suspendBlockingLazy {
            Thread.sleep(Random.nextLong(100))
            log.debug { "Calculate lazy value in blocking mode." }
            calledCounter.incrementAndGet()
            42
        }
        called shouldBeEqualTo 0

        yield()

        lazyValue() shouldBeEqualTo 42
        lazyValue() shouldBeEqualTo 42

        called shouldBeEqualTo 1
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `get lazy value in blocking mode with IO dispatchers`() = runTest {
        val calledCounter = atomic(0)
        val called by calledCounter

        val lazyValue = suspendBlockingLazyIO {
            Thread.sleep(Random.nextLong(100))
            log.debug { "Calculate lazy value in blocking mode with IO dispatchers" }
            calledCounter.incrementAndGet()
            42
        }
        called shouldBeEqualTo 0

        yield()

        val lazy1 = async { lazyValue() }
        val lazy2 = async { lazyValue() }

        yield()

        lazy1.await() shouldBeEqualTo 42
        lazy2.await() shouldBeEqualTo 42

        called shouldBeEqualTo 1
    }
}
