package io.bluetape4k.utils.jwt.reader

import io.bluetape4k.core.LibraryName
import io.bluetape4k.infra.cache.jcache.JCaching
import io.bluetape4k.infra.cache.nearcache.NearCache
import io.bluetape4k.infra.cache.nearcache.NearCacheConfig
import io.bluetape4k.logging.KLogging
import io.bluetape4k.testcontainers.storage.RedisServer
import io.bluetape4k.utils.jwt.AbstractJwtTest
import io.bluetape4k.utils.jwt.codec.Lz4Codec
import io.bluetape4k.utils.jwt.provider.JwtProviderFactory
import org.amshove.kluent.shouldBeEqualTo
import org.awaitility.kotlin.atMost
import org.awaitility.kotlin.await
import org.awaitility.kotlin.until
import org.junit.jupiter.api.Test
import java.time.Duration
import java.util.*
import javax.cache.Cache

class JwtReaderCachingTest: AbstractJwtTest() {

    companion object: KLogging() {
        private val frontCache1: Cache<String, JwtReaderDto> by lazy {
            JCaching.Caffeine.getOrCreate("caffeine-reader-1")
        }

        private val frontCache2: Cache<String, JwtReaderDto> by lazy {
            JCaching.EhCache.getOrCreate("ehcache-reader-2")
        }

        private val redissonConfig by lazy {
            RedisServer.Launcher.RedissonLib.getRedissonConfig().apply {
                // 압축을 하면 데이터 사이즈가 감소하지만, 굳이 할 필요는 없다
                // codec = LZ4Codec()
            }
        }

        private val backCache: Cache<String, JwtReaderDto> by lazy {
            JCaching.Redisson.getOrCreate("jwt-back-cache-reader", redissonConfig)
        }
    }

    private val jwtProvider = JwtProviderFactory.default()
    private val createdAt = System.currentTimeMillis()

    private val jwt = jwtProvider.compose {
        header("x-publisher", LibraryName)
        issuedAt = Date(createdAt)
        issuer = LibraryName
        claim("service", LibraryName)

        compressionCodec = Lz4Codec()
    }

    private val reader = jwtProvider.parse(jwt)

    @Test
    fun `caching reader with near cache`() {
        frontCache1.put(jwt, reader.toDto())
        val actual = frontCache1.get(jwt)!!.toJwtReader()

        assertSameReader(reader, actual)
    }

    @Test
    fun `caching reader with remote cache`() {
        backCache.put(jwt, reader.toDto())
        val actual = backCache.get(jwt)!!.toJwtReader()
        assertSameReader(reader, actual)

        frontCache1.put(jwt, actual.toDto())
        val actual2 = frontCache1.get(jwt)!!.toJwtReader()
        assertSameReader(actual, actual2)
    }

    // NOTE: Redisson 3.19.1 사용 시, NearCache 에서 kotlin.collections.LinkedHashMap 에 대해 역직렬화에 실패하는 버그가 있습니다.
    // NOTE: java.util.HashMap 을 사용하면, 성공합니다.
    // 참고: https://github.com/redisson/redisson/issues/4809
    @Test
    fun `caching reader with two near cache`() {
        val nearCacheConfig1 = NearCacheConfig<String, JwtReaderDto>(isSynchronous = true)
        val nearCacheConfig2 = NearCacheConfig<String, JwtReaderDto>(isSynchronous = true)
        val nearCache1 = NearCache(nearCacheConfig1, backCache)
        val nearCache2 = NearCache(nearCacheConfig2, backCache)

        // Cache 1 에서 저장
        nearCache1.put(jwt, reader.toDto())
        nearCache1.get(jwt)!!.toJwtReader() shouldBeEqualTo reader

        await atMost Duration.ofMillis(100) until { nearCache2.containsKey(jwt) }

        // Cache 2 에서 조회
        val actual = nearCache2.get(jwt)!!.toJwtReader()

        assertSameReader(reader, actual)
    }

    private fun assertSameReader(expected: JwtReader, actual: JwtReader) {
        actual.header<String>("x-publisher") shouldBeEqualTo expected.header("x-publisher")
        actual.claim<String>("service") shouldBeEqualTo expected.claim<String>("service")
        actual.issuedAt shouldBeEqualTo expected.issuedAt
        actual.issuer shouldBeEqualTo expected.issuer
    }
}
