package io.bluetape4k.bucket4j.local

import io.bluetape4k.bucket4j.coroutines.CoLocalBucket
import io.bluetape4k.logging.KLogging
import io.github.bucket4j.BucketConfiguration
import io.github.bucket4j.MathType
import io.github.bucket4j.TimeMeter

/**
 * Custom key 기준으로 [CoLocalBucket]을 제공하는 Provider 입니다.
 *
 * @param bucketConfiguration [BucketConfiguration] 인스턴스
 * @param keyPrefix Bucket Key Prefix
 */
open class LocalCoBucketProvider(
    bucketConfiguration: BucketConfiguration,
    keyPrefix: String = DEFAULT_KEY_PREFIX,
): AbstractLocalBucketProvider(bucketConfiguration, keyPrefix) {

    companion object: KLogging()

    /**
     * Coroutines용 [CoLocalBucket]을 생성합니다.
     *
     * @return [CoLocalBucket] 인스턴스
     */
    override fun createBucket(): CoLocalBucket {
        return CoLocalBucket(
            bucketConfiguration,
            MathType.INTEGER_64_BITS,
            TimeMeter.SYSTEM_MILLISECONDS
        )
    }
}
