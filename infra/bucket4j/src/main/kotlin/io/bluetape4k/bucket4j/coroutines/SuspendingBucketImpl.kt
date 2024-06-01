package io.bluetape4k.bucket4j.coroutines

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.github.bucket4j.BucketConfiguration
import io.github.bucket4j.BucketExceptions
import io.github.bucket4j.LimitChecker
import io.github.bucket4j.MathType
import io.github.bucket4j.TimeMeter
import io.github.bucket4j.local.LockFreeBucket
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlin.time.Duration
import kotlin.time.Duration.Companion.nanoseconds

@Deprecated("use CoLocalBucket instead")
class SuspendingBucketImpl private constructor(
    bucketConfiguration: BucketConfiguration,
    timeMeter: TimeMeter,
    mathType: MathType,
): LockFreeBucket(bucketConfiguration, mathType, timeMeter) {

    companion object: KLogging() {
        @Suppress("DEPRECATION")
        @JvmStatic
        operator fun invoke(
            config: SuspendingBucketConfiguration,
            mathType: MathType = MathType.INTEGER_64_BITS,
        ): SuspendingBucketImpl {
            return SuspendingBucketImpl(
                config.toBucketConfiguration(),
                config.timeMeter,
                mathType
            )
        }

        @Suppress("DEPRECATION")
        @JvmStatic
        operator fun invoke(
            config: BucketConfiguration,
            timeMeter: TimeMeter = TimeMeter.SYSTEM_MILLISECONDS,
            mathType: MathType = MathType.INTEGER_64_BITS,
        ): SuspendingBucketImpl {
            return SuspendingBucketImpl(config, timeMeter, mathType)
        }
    }

    suspend fun tryConsumeSuspending(tokensToConsume: Long, maxWaitTime: Duration): Boolean = coroutineScope {
        LimitChecker.checkTokensToConsume(tokensToConsume)
        val maxWaitTimeNanos: Long = maxWaitTime.inWholeNanoseconds
        LimitChecker.checkMaxWaitTime(maxWaitTimeNanos)

        val nanosToDelay: Long = reserveAndCalculateTimeToSleepImpl(tokensToConsume, maxWaitTimeNanos)
        log.debug { "nanosToDelay=$nanosToDelay" }

        if (nanosToDelay == INFINITY_DURATION) {
            log.debug { "rejected. nanosToDelay is INFINITY_DURATION" }
            listener.onRejected(tokensToConsume)
            return@coroutineScope false
        }

        listener.onConsumed(tokensToConsume)
        if (nanosToDelay > 0L) {
            delay(nanosToDelay.nanoseconds)
            listener.onParked(nanosToDelay)
        }

        return@coroutineScope true
    }

    suspend fun consumeSuspending(tokensToConsume: Long) = coroutineScope {
        LimitChecker.checkTokensToConsume(tokensToConsume)

        val nanosToDelay: Long = reserveAndCalculateTimeToSleepImpl(tokensToConsume, INFINITY_DURATION)
        log.debug { "nanosToDelay=$nanosToDelay" }

        if (nanosToDelay == INFINITY_DURATION) {
            throw BucketExceptions.reservationOverflow()
        }

        listener.onConsumed(tokensToConsume)
        if (nanosToDelay > 0L) {
            delay(nanosToDelay.nanoseconds)
        }
    }
}
