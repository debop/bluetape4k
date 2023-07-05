package io.bluetape4k.examples.redisson.coroutines.objects

import io.bluetape4k.collections.eclipse.fastList
import io.bluetape4k.data.redis.redisson.coroutines.awaitSuspending
import io.bluetape4k.examples.redisson.coroutines.AbstractRedissonCoroutineTest
import io.bluetape4k.junit5.concurrency.MultithreadingTester
import io.bluetape4k.junit5.coroutines.MultiJobTester
import io.bluetape4k.junit5.coroutines.runSuspendWithIO
import io.bluetape4k.logging.KLogging
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeFalse
import org.amshove.kluent.shouldBeTrue
import org.junit.jupiter.api.RepeatedTest

class AtomicLongExamples: AbstractRedissonCoroutineTest() {

    companion object: KLogging() {
        private const val REPEAT_SIZE = 3
        private const val TEST_COUNT = 1000
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `AtomicLog in coroutines`() = runSuspendWithIO {
        val counter = redisson.getAtomicLong(randomName())
        val jobs = fastList(TEST_COUNT) {
            scope.launch {
                counter.incrementAndGetAsync().awaitSuspending()
            }
        }
        jobs.joinAll()

        counter.getAsync().awaitSuspending() shouldBeEqualTo TEST_COUNT.toLong()
        counter.deleteAsync().awaitSuspending().shouldBeTrue()
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `AtomicLog operatiions`() = runSuspendWithIO {
        val counter = redisson.getAtomicLong(randomName())

        counter.setAsync(0).awaitSuspending()
        counter.addAndGetAsync(10L).awaitSuspending() shouldBeEqualTo 10L

        counter.compareAndSetAsync(-1L, 42L).awaitSuspending().shouldBeFalse()
        counter.compareAndSetAsync(10L, 42L).awaitSuspending().shouldBeTrue()

        counter.decrementAndGetAsync().awaitSuspending() shouldBeEqualTo 41L
        counter.incrementAndGetAsync().awaitSuspending() shouldBeEqualTo 42L

        counter.getAndAddAsync(3L).awaitSuspending() shouldBeEqualTo 42L

        counter.getAndDecrementAsync().awaitSuspending() shouldBeEqualTo 45L
        counter.getAndIncrementAsync().awaitSuspending() shouldBeEqualTo 44L

        counter.deleteAsync().awaitSuspending().shouldBeTrue()
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `AtomicLong in Multi job`() = runSuspendWithIO {
        val counter = redisson.getAtomicLong(randomName())

        MultiJobTester()
            .numThreads(8)
            .roundsPerThread(32)
            .add {
                counter.incrementAndGetAsync().awaitSuspending()
            }
            .run()

        counter.async.awaitSuspending() shouldBeEqualTo 8 * 32L
        counter.deleteAsync().awaitSuspending().shouldBeTrue()
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `AtomicLong in Multi threading`() {
        val counter = redisson.getAtomicLong(randomName())

        MultithreadingTester()
            .numThreads(32)
            .roundsPerThread(8)
            .add {
                counter.incrementAndGet()
            }
            .run()

        counter.get() shouldBeEqualTo 32 * 8L
        counter.delete().shouldBeTrue()
    }
}
