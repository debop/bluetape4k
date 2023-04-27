package io.bluetape4k.examples.redisson.coroutines.objects

import io.bluetape4k.data.redis.redisson.coroutines.awaitSuspending
import io.bluetape4k.examples.redisson.coroutines.AbstractRedissonCoroutineTest
import io.bluetape4k.junit5.coroutines.runSuspendWithIO
import io.bluetape4k.logging.KLogging
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import org.amshove.kluent.shouldBeFalse
import org.amshove.kluent.shouldBeTrue
import org.junit.jupiter.api.Test
import org.redisson.api.RateIntervalUnit
import org.redisson.api.RateType


class RateLimiterExamples: AbstractRedissonCoroutineTest() {

    companion object: KLogging()

    @Test
    fun `rate limiter 를 이용하여 요청 제한하기`() = runSuspendWithIO {
        val limiter = redisson.getRateLimiter(randomName())

        // 2초 동안 5개의 request 만 허용
        limiter.trySetRateAsync(RateType.OVERALL, 5, 100, RateIntervalUnit.SECONDS).awaitSuspending()

        // 3개
        limiter.tryAcquireAsync(1).awaitSuspending().shouldBeTrue()
        limiter.tryAcquireAsync(1).awaitSuspending().shouldBeTrue()
        limiter.tryAcquireAsync(1).awaitSuspending().shouldBeTrue()

        val job = scope.launch {
            // 2개
            limiter.tryAcquireAsync(1).awaitSuspending().shouldBeTrue()
            limiter.tryAcquireAsync(1).awaitSuspending().shouldBeTrue()
            yield()

            // 5개 모두 소진됨
            limiter.tryAcquireAsync(1).awaitSuspending().shouldBeFalse()
        }
        yield()
        job.join()
        yield()

        // 5개 모두 소진됨
        limiter.tryAcquireAsync(1).awaitSuspending().shouldBeFalse()

        limiter.deleteAsync().awaitSuspending()
    }

    @Test
    fun `RedissonClient 인스턴스 별로 rate limit 를 따로 허용한다`() = runSuspendWithIO {
        val limiterName = randomName()
        val limiter = redisson.getRateLimiter(limiterName)

        // 2초 동안 각 client 별로 3개의 request 만 허용
        limiter.trySetRateAsync(RateType.PER_CLIENT, 3, 100, RateIntervalUnit.SECONDS).awaitSuspending()

        // limiter는 3개 모두 소진
        limiter.tryAcquireAsync(1).awaitSuspending().shouldBeTrue()
        limiter.tryAcquireAsync(1).awaitSuspending().shouldBeTrue()
        limiter.tryAcquireAsync(1).awaitSuspending().shouldBeTrue()

        val job = scope.launch {
            // 다른 client 에서 limiter 생성
            val redisson2 = newRedisson()
            try {
                val limiter2 = redisson2.getRateLimiter(limiterName)
                // limiter2는 3개 모두 소진
                limiter2.tryAcquireAsync(1).awaitSuspending().shouldBeTrue()
                limiter2.tryAcquireAsync(1).awaitSuspending().shouldBeTrue()
                limiter2.tryAcquireAsync(1).awaitSuspending().shouldBeTrue()
                yield()

                // limiter2는 모두 소진됨
                limiter2.tryAcquireAsync(1).awaitSuspending().shouldBeFalse()
            } finally {
                redisson2.shutdown()
            }
        }
        yield()
        job.join()

        yield()
        // limiter는 모두 소진됨
        limiter.tryAcquireAsync(1).awaitSuspending().shouldBeFalse()

        limiter.deleteAsync().awaitSuspending()
    }
}
