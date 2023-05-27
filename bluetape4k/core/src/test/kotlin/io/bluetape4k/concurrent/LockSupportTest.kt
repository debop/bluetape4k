package io.bluetape4k.concurrent

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.trace
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.RepeatedTest
import java.util.concurrent.locks.ReentrantReadWriteLock

class LockSupportTest {

    companion object: KLogging() {
        private const val REPEAT_SIZE = 10
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `with CountDownLatch`() {
        val result = withLatch(1) {
            log.trace { "with CountDownLatch ..." }
            Thread.sleep(10)
            countDown()
            log.trace { "countDown latch ..." }
            42
        }

        result shouldBeEqualTo 42
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `with readLock`() = runTest {
        var counter = 0

        val rwLock = ReentrantReadWriteLock()

        val result1 = async {
            rwLock.withWriteLock {
                delay(200)
                counter = 42
            }
            rwLock.withReadLock {
                delay(20)
                counter
            }
        }

        val result2 = async {
            rwLock.withWriteLock {
                delay(20)
                counter = 21
            }
            rwLock.withReadLock {
                delay(200)
                counter
            }
        }

        log.trace { "result1=${result1.await()}, result2=${result2.await()}" }
        result1.await() shouldBeEqualTo 42
        result2.await() shouldBeEqualTo 42
    }
}
