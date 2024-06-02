package io.bluetape4k.examples.redisson.coroutines.locks

import io.bluetape4k.examples.redisson.coroutines.AbstractRedissonCoroutineTest
import io.bluetape4k.junit5.concurrency.MultithreadingTester
import io.bluetape4k.logging.KLogging
import io.bluetape4k.redis.redisson.coroutines.coAwait
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.yield
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeTrue
import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit


/**
 * Semaphore examples
 *
 * 참고: [Semaphore](https://github.com/redisson/redisson/wiki/8.-distributed-locks-and-synchronizers/#86-semaphore)
 */
class SemaphoreExamples: AbstractRedissonCoroutineTest() {

    companion object: KLogging()

    @Test
    fun `semaphore example`() = runTest {
        val semaphoreName = randomName()
        val semaphore = redisson.getSemaphore(semaphoreName)

        // 5개 확보
        semaphore.trySetPermitsAsync(5).coAwait().shouldBeTrue()

        // 3개 획득
        semaphore.acquireAsync(3).coAwait()

        val job = scope.launch {
            val redisson2 = newRedisson()
            try {
                val s2 = redisson2.getSemaphore(semaphoreName)
                yield()
                // 2개 반납 (4개 남음)
                s2.releaseAsync(2).coAwait()
                yield()
            } finally {
                redisson2.shutdown()
            }
        }

        val job2 = scope.launch {
            val redisson3 = newRedisson()
            try {
                val s3 = redisson3.getSemaphore(semaphoreName)
                yield()
                // 4개 확보
                s3.tryAcquireAsync(4, 5, TimeUnit.SECONDS).coAwait().shouldBeTrue()
                yield()
            } finally {
                redisson3.shutdown()
            }
        }
        yield()

        job.join()
        job2.join()

        semaphore.availablePermitsAsync().coAwait() shouldBeEqualTo 0

        // 4개 반납
        semaphore.releaseAsync(4).coAwait()
        semaphore.availablePermitsAsync().coAwait() shouldBeEqualTo 4

        // 여유분을 모두 획득합니다.
        semaphore.drainPermitsAsync().coAwait() shouldBeEqualTo 4
        semaphore.availablePermitsAsync().coAwait() shouldBeEqualTo 0

        semaphore.deleteAsync().coAwait()
    }

    @Test
    fun `semaphore in multi threading`() {
        val semaphoreName = randomName()
        val semaphore = redisson.getSemaphore(semaphoreName)

        // 5개 확보
        semaphore.trySetPermits(5).shouldBeTrue()

        // 3개 획득
        semaphore.acquire(3)

        MultithreadingTester()
            .numThreads(8)
            .roundsPerThread(4)
            .add {
                val redisson2 = newRedisson()
                try {
                    val s2 = redisson2.getSemaphore(semaphoreName)
                    Thread.sleep(1)
                    // 2개 반납 (4개 남음)
                    s2.release(2)
                    Thread.sleep(1)
                } finally {
                    redisson2.shutdown()
                }
            }
            .add {
                val redisson3 = newRedisson()
                try {
                    val s3 = redisson3.getSemaphore(semaphoreName)
                    Thread.sleep(1)
                    // 4개 확보
                    s3.tryAcquire(2, 5, TimeUnit.SECONDS).shouldBeTrue()
                    Thread.sleep(1)
                } finally {
                    redisson3.shutdown()
                }
            }
            .run()

        semaphore.availablePermits() shouldBeEqualTo 2

        // 4개 반납
        semaphore.release(4)
        semaphore.availablePermits() shouldBeEqualTo 6

        // 여유분을 모두 획득합니다.
        semaphore.drainPermits() shouldBeEqualTo 6
        semaphore.availablePermits() shouldBeEqualTo 0

        semaphore.delete()
    }
}
