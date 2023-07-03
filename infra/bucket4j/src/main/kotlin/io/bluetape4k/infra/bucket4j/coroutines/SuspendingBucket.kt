package io.bluetape4k.infra.bucket4j.coroutines

import io.bluetape4k.logging.KLogging
import io.github.bucket4j.Bucket
import io.github.bucket4j.MathType
import kotlin.time.Duration

/**
 * [SuspendingBucket] is an opaque wrapper around [Bucket4j's](https://github.com/bucket4j/bucket4j) LockFreeBucket,
 * and implements an interface semantically equivalent to [BlockingBucket](https://bucket4j.com/8.2.0/toc.html#blocking-bucket).
 * Whereas Bucket4j's blocking behavior is just that, [SuspendingBucket] instead delays, making it safe to use in a
 * coroutine context.
 */
class SuspendingBucket private constructor(val impl: SuspendingBucketImpl): Bucket by impl {

    companion object: KLogging() {
        @JvmStatic
        operator fun invoke(configure: SuspendingBucketConfiguration.() -> Unit = {}): SuspendingBucket {
            val config = SuspendingBucketConfiguration().apply(configure)
            val impl = SuspendingBucketImpl(config, MathType.INTEGER_64_BITS)
            return SuspendingBucket(impl)
        }
    }

    suspend fun tryConsume(tokensToConsume: Long, maxWaitTime: Duration): Boolean {
        return impl.tryConsumeSuspending(tokensToConsume, maxWaitTime)
    }

    suspend fun consume(tokensToConsume: Long) {
        impl.consumeSuspending(tokensToConsume)
    }
}
