package io.bluetape4k.core.concurrency

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.trace
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.RepeatedTest
import java.util.concurrent.locks.ReentrantReadWriteLock

class LockSupportTest {

    companion object : KLogging() {
        private const val REPEAT_SIZE = 10
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `with CountDownLatch`() {

        val x = 1.withLatch {
            log.trace { "with CountDownLatch ..." }
            Thread.sleep(10)
            countDown()
            log.trace { "countDown latch ..." }
            42
        }

        x shouldBeEqualTo 42
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `with readLock`() = runBlocking<Unit> {
        var counter = 0

        val rwLock = ReentrantReadWriteLock()

        val result1 = async {
            rwLock.withWriteLock {
                delay(20)
                counter = 42
            }
            rwLock.withReadLock {
                delay(2)
                counter
            }
        }

        val result2 = async {
            rwLock.withWriteLock {
                delay(2)
                counter = 21
            }
            rwLock.withReadLock {
                delay(20)
                counter
            }
        }

        log.trace { "result1=${result1.await()}, result2=${result2.await()}" }
        result1.await() shouldBeEqualTo 42
        result2.await() shouldBeEqualTo 42
    }
}