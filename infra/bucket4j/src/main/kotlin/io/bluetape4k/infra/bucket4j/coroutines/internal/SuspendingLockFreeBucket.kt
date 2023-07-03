package io.bluetape4k.infra.bucket4j.coroutines.internal

import io.bluetape4k.infra.bucket4j.coroutines.SuspendingBucketConfiguration
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.github.bucket4j.BucketConfiguration
import io.github.bucket4j.BucketExceptions
import io.github.bucket4j.LimitChecker
import io.github.bucket4j.MathType
import io.github.bucket4j.TimeMeter
import io.github.bucket4j.local.LockFreeBucket
import kotlinx.coroutines.delay
import kotlin.time.Duration
import kotlin.time.Duration.Companion.nanoseconds

open class SuspendingLockFreeBucket private constructor(
    bucketConfiguration: BucketConfiguration,
    timeMeter: TimeMeter,
    mathType: MathType,
): LockFreeBucket(bucketConfiguration, mathType, timeMeter) {

    companion object: KLogging() {
        @JvmStatic
        operator fun invoke(
            config: SuspendingBucketConfiguration,
            mathType: MathType = MathType.INTEGER_64_BITS,
        ): SuspendingLockFreeBucket {
            return SuspendingLockFreeBucket(
                config.toBucketConfiguration(),
                config.timeMeter,
                mathType
            )
        }
    }

    suspend fun tryConsumeSuspending(tokensToConsume: Long, maxWaitTime: Duration): Boolean {
        LimitChecker.checkTokensToConsume(tokensToConsume)
        val maxWaitTimeNanos = maxWaitTime.inWholeNanoseconds
        LimitChecker.checkMaxWaitTime(maxWaitTimeNanos)

        val nanosToDelay = reserveAndCalculateTimeToSleepImpl(tokensToConsume, maxWaitTimeNanos)
        log.debug { "nanosToDelay=$nanosToDelay" }

        if (nanosToDelay == INFINITY_DURATION) {
            log.debug { "rejected. nanosToDelay is INFINITY_DURATION" }
            listener.onRejected(tokensToConsume)
            return false
        }

        listener.onConsumed(tokensToConsume)
        if (nanosToDelay > 0L) {
            delay(nanosToDelay.nanoseconds)
            listener.onParked(nanosToDelay)
        }

        return true
    }

    suspend fun consumeSuspending(tokensToConsume: Long) {
        LimitChecker.checkTokensToConsume(tokensToConsume)

        val nanosToDelay = reserveAndCalculateTimeToSleepImpl(tokensToConsume, INFINITY_DURATION)
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
