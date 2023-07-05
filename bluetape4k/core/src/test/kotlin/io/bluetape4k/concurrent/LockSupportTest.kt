package io.bluetape4k.concurrent

import io.bluetape4k.junit5.concurrency.MultithreadingTester
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.trace
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.Test
import java.time.Duration
import java.util.concurrent.TimeoutException
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.test.assertFailsWith

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
    fun `with CountDownLatch with Timeout`() {
        val result = withLatch(1, Duration.ofSeconds(1)) {
            log.trace { "with CountDownLatch ..." }
            Thread.sleep(10)
            countDown()
            log.trace { "countDown latch ..." }
            42
        }

        result shouldBeEqualTo 42
    }

    @Test
    fun `long task with CountDownLatch with Timeout`() {
        assertFailsWith<TimeoutException> {
            withLatch(1, Duration.ofMillis(100)) {
                log.trace { "with CountDownLatch ..." }
                Thread.sleep(200)
                countDown()
                log.trace { "countDown latch ..." }
                42
            }
        }
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

    @Test
    fun `read and write lock in multi threading`() {
        val lock = ReentrantReadWriteLock()
        var counter = 0

        MultithreadingTester()
            .numThreads(16)
            .roundsPerThread(2)
            .add {
                lock.withReadLock {
                    Thread.sleep(10)
                    val current = counter
                    log.trace { "current=$current" }
                }
            }
            .add {
                lock.withWriteLock {
                    Thread.sleep(20)
                    counter++
                }
            }
            .run()

        counter shouldBeEqualTo 16
    }
}
