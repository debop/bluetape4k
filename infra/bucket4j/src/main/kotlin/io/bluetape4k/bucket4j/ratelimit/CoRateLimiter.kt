package io.bluetape4k.bucket4j.ratelimit

/**
 * Coroutine 환경에서의 Rate Limiter 인터페이스
 *
 * @param K
 * @constructor Create empty Co rate limiter
 */
interface CoRateLimiter<K> {

    /**
     * [key] 기준으로 [numToken] 갯수만큼 소비합니다. 결과는 [RateLimitResult]로 반환됩니다.
     *
     * @param key      Rate Limit 적용 대상 Key
     * @param numToken 소비할 토큰 수
     * @return [RateLimitResult] 토큰 소비 결과
     */
    suspend fun coConsume(key: K, numToken: Long = 1): RateLimitResult

}
