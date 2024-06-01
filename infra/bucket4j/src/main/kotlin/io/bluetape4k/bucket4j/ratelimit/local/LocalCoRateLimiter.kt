package io.bluetape4k.bucket4j.ratelimit.local

import io.bluetape4k.bucket4j.local.LocalCoBucketProvider
import io.bluetape4k.bucket4j.ratelimit.CoRateLimiter
import io.bluetape4k.bucket4j.ratelimit.RateLimitResult
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.logging.warn
import io.bluetape4k.support.requireNotBlank

/**
 * 로컬 환경에서 Rate Limiter 를 적용하는 Coroutine Rate Limiter 구현체
 *
 * @property bucketProvider [LocalCoBucketProvider] 인스턴스
 */
class LocalCoRateLimiter(
    private val bucketProvider: LocalCoBucketProvider,
): CoRateLimiter<String> {

    companion object: KLogging()

    /**
     * [key] 기준으로 [numToken] 갯수만큼 소비합니다. 결과는 [RateLimitResult]로 반환됩니다.
     *
     * @param key      Rate Limit 적용 대상 Key
     * @param numToken 소비할 토큰 수
     * @return [RateLimitResult] 토큰 소비 결과
     */
    override suspend fun coConsume(key: String, numToken: Long): RateLimitResult {
        key.requireNotBlank("key")
        log.debug { "rate limit for key=$key, numToken=$numToken" }

        return try {
            val bucketProxy = bucketProvider.resolveBucket(key)

            if (bucketProxy.tryConsume(numToken)) {
                RateLimitResult(numToken, bucketProxy.availableTokens)
            } else {
                RateLimitResult(0, bucketProxy.availableTokens)
            }
        } catch (e: Throwable) {
            log.warn(e) { "Rate Limiter 적용에 실패했습니다. key=$key" }
            RateLimitResult.ERROR
        }
    }
}
