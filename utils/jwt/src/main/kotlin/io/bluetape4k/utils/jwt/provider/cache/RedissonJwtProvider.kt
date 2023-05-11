package io.bluetape4k.utils.jwt.provider.cache

import io.bluetape4k.logging.KLogging
import io.bluetape4k.utils.jwt.provider.JwtProvider
import io.bluetape4k.utils.jwt.reader.JwtReader
import io.bluetape4k.utils.jwt.reader.JwtReaderDto
import io.bluetape4k.utils.jwt.reader.isExpired
import io.bluetape4k.utils.jwt.reader.toDto
import io.bluetape4k.utils.jwt.reader.toJwtReader
import org.redisson.api.RMapCache
import java.util.concurrent.TimeUnit

/**
 * JWT 파싱된 정보를 Redis에 캐싱하여 반복적인 Parsing 시간을 절약합니다.
 * Redisson의 경우 캐시 엔트리의 유효기간을 따로 설정할 수 있습니다.
 *
 * @property cache Redisson [RMapCache] 인스턴스
 * @property ttl   캐시 엔트리의 유효기간 (기본값: 3일)
 * @property delegate [JwtProvider] 인스턴스
 * @constructor Create empty Redisson jwt provider
 */
class RedissonJwtProvider(
    private val cache: RMapCache<String, JwtReaderDto>,
    private val delegate: JwtProvider,
    private val ttl: Long = DEFAULT_TTL,
): JwtProvider by delegate {

    companion object: KLogging() {
        const val DEFAULT_TTL: Long = 3 * 24 * 60_000L
    }

    override fun parse(jwtString: String): JwtReader {
        val reader = cache.get(jwtString)?.toJwtReader()
        return if (reader != null) {
            if (reader.isExpired) {
                cache.fastRemove(jwtString)
            }
            reader
        } else {
            delegate.parse(jwtString).apply {
                if (!isExpired) {
                    // JWT 토큰의 유효기간 또는 기본 TTL 값 중 작은 값을 TTL 로 삼는다
                    // 기본 TTL은 3일이지만 JWT 토큰의 유효기간이 1일인 경우 TTL은 1일로 설정된다
                    val readerTtl = this.expiration.time - System.currentTimeMillis()
                    cache.fastPut(jwtString, this.toDto(), minOf(ttl, readerTtl), TimeUnit.MILLISECONDS)
                }
            }
        }
    }
}
