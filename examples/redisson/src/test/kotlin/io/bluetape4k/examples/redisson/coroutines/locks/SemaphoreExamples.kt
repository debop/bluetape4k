package io.bluetape4k.examples.redisson.coroutines.locks

import io.bluetape4k.data.redis.redisson.coroutines.awaitSuspending
import io.bluetape4k.examples.redisson.coroutines.AbstractRedissonCoroutineTest
import io.bluetape4k.logging.KLogging
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.yield
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeTrue
import org.junit.jupiter.api.Test


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
        semaphore.trySetPermitsAsync(5).awaitSuspending().shouldBeTrue()

        // 3개 획득
        semaphore.acquireAsync(3).awaitSuspending()

        val job = scope.launch {
            val redisson2 = newRedisson()
            try {
                val s2 = redisson2.getSemaphore(semaphoreName)
                yield()
                // 2개 반납 (4개 남음)
                s2.releaseAsync(2).awaitSuspending()
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
                s3.tryAcquireAsync(4, 5, TimeUnit.SECONDS).awaitSuspending().shouldBeTrue()
                yield()
            } finally {
                redisson3.shutdown()
            }
        }
        yield()

        job.join()
        job2.join()

        semaphore.availablePermitsAsync().awaitSuspending() shouldBeEqualTo 0

        // 4개 반납
        semaphore.releaseAsync(4).awaitSuspending()
        semaphore.availablePermitsAsync().awaitSuspending() shouldBeEqualTo 4

        // 여유분을 모두 획득합니다.
        semaphore.drainPermitsAsync().awaitSuspending() shouldBeEqualTo 4
        semaphore.availablePermitsAsync().awaitSuspending() shouldBeEqualTo 0

        semaphore.deleteAsync().awaitSuspending()
    }
}
