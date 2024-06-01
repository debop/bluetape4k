package io.bluetape4k.bucket4j.ratelimit.distributed

import io.bluetape4k.bucket4j.TestRedisServer
import io.bluetape4k.bucket4j.distributed.AsyncBucketProxyProvider
import io.bluetape4k.bucket4j.distributed.redis.redissonBasedProxyManagerOf
import io.bluetape4k.bucket4j.ratelimit.AbstractCoRateLimiterTest
import io.bluetape4k.bucket4j.ratelimit.CoRateLimiter
import io.bluetape4k.logging.KLogging
import io.github.bucket4j.distributed.ExpirationAfterWriteStrategy
import io.github.bucket4j.distributed.proxy.ClientSideConfig
import io.github.bucket4j.distributed.proxy.ExecutionStrategy
import java.util.concurrent.Executors
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

class RedissonCoRateLimiterTest: AbstractCoRateLimiterTest() {

    companion object: KLogging()

    val bucketProvider: AsyncBucketProxyProvider by lazy {

        val redisson = TestRedisServer.redissonClient()

        val redissonProxyManager = redissonBasedProxyManagerOf(redisson) {
            withClientSideConfig(
                ClientSideConfig.getDefault()
                    .withExpirationAfterWriteStrategy(
                        ExpirationAfterWriteStrategy.basedOnTimeForRefillingBucketUpToMax(
                            90.seconds.toJavaDuration()
                        )
                    )
                    .withExecutionStrategy(ExecutionStrategy.background(Executors.newVirtualThreadPerTaskExecutor()))
            )
        }

        AsyncBucketProxyProvider(redissonProxyManager.asAsync(), defaultBucketConfiguration)
    }

    override val rateLimiter: CoRateLimiter<String> by lazy {
        DistributedCoRateLimiter(bucketProvider)
    }

}
