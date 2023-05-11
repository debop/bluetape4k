package io.bluetape4k.utils.jwt.provider.cache

import io.bluetape4k.utils.jwt.provider.JwtProvider
import io.bluetape4k.utils.jwt.reader.JwtReader
import io.bluetape4k.utils.jwt.reader.JwtReaderDto
import io.bluetape4k.utils.jwt.reader.isExpired
import io.bluetape4k.utils.jwt.reader.toDto
import io.bluetape4k.utils.jwt.reader.toJwtReader
import javax.cache.Cache

/**
 * JWT 파싱된 정보를 Java Cache에 캐싱하여 반복적인 Parsing 시간을 절약합니다.
 *
 * @property cache    JwtReaderDto를 저장할 Javax Cache
 * @property delegate JWT 를 파싱하는 [JwtProvider] 인스턴스
 */
class JCacheJwtProvider(
    private val cache: Cache<String, JwtReaderDto>,
    private val delegate: JwtProvider,
): JwtProvider by delegate {

    override fun parse(jwtString: String): JwtReader {
        val reader = cache.get(jwtString)?.toJwtReader()
        return if (reader != null) {
            if (reader.isExpired) {
                cache.remove(jwtString)
            }
            reader
        } else {
            delegate.parse(jwtString).apply {
                if (!isExpired) {
                    cache.put(jwtString, toDto())
                }
            }
        }
    }
}
