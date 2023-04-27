package io.bluetape4k.examples.redisson.coroutines.objects

import io.bluetape4k.data.redis.redisson.coroutines.awaitSuspending
import io.bluetape4k.examples.redisson.coroutines.AbstractRedissonCoroutineTest
import io.bluetape4k.junit5.coroutines.runSuspendWithIO
import io.bluetape4k.logging.KLogging
import kotlinx.coroutines.delay
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeFalse
import org.amshove.kluent.shouldBeTrue
import org.junit.jupiter.api.Test

class AtomicLongExamples: AbstractRedissonCoroutineTest() {

    companion object: KLogging() {
        private const val TEST_COUNT = 10_000
    }

    @Test
    fun `AtomicLog in coroutines`() = runSuspendWithIO {
        val counter = redisson.getAtomicLong(randomName())
        val jobs = List(TEST_COUNT) {
            scope.launch {
                counter.incrementAndGetAsync().awaitSuspending()
            }
        }
        delay(10)
        jobs.joinAll()

        counter.getAsync().awaitSuspending() shouldBeEqualTo TEST_COUNT.toLong()

        counter.deleteAsync().awaitSuspending()
    }

    @Test
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

        counter.deleteAsync().awaitSuspending()
    }
}
