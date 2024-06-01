package io.bluetape4k.bucket4j.local

import io.bluetape4k.logging.KLogging
import io.github.bucket4j.Bucket
import io.github.bucket4j.BucketConfiguration
import io.github.bucket4j.MathType
import io.github.bucket4j.local.LocalBucket
import io.github.bucket4j.local.SynchronizationStrategy

/**
 * Custom Key 기반으로 [LocalBucket]을 제공합니다.
 *
 * @constructor
 *
 * @param bucketConfiguration
 * @param keyPrefix
 */
open class LocalBucketProvider(
    bucketConfiguration: BucketConfiguration,
    keyPrefix: String = DEFAULT_KEY_PREFIX,
): AbstractLocalBucketProvider(bucketConfiguration, keyPrefix) {
    companion object: KLogging()

    /**
     * Local에서 사용하는 [LocalBucket] 을 생성합니다.
     *
     * @return [LocalBucket]을 반환합니다.
     */
    override fun createBucket(): LocalBucket {
        val builder = Bucket.builder()
            .withSynchronizationStrategy(SynchronizationStrategy.LOCK_FREE)
            .withMath(MathType.INTEGER_64_BITS)
            .withMillisecondPrecision()

        bucketConfiguration.bandwidths.forEach {
            builder.addLimit(it)
        }

        return builder.build()
    }
}
