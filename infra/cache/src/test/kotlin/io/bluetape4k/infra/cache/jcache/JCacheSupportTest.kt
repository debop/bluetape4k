package io.bluetape4k.infra.cache.jcache

import com.github.benmanes.caffeine.jcache.spi.CaffeineCachingProvider
import io.bluetape4k.logging.KLogging
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeFalse
import org.amshove.kluent.shouldBeNull
import org.amshove.kluent.shouldBeTrue
import org.amshove.kluent.shouldNotBeNull
import org.ehcache.jsr107.EhcacheCachingProvider
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import javax.cache.CacheManager

class JCacheSupportTest {

    companion object: KLogging() {
        @JvmStatic
        fun getJCacheManagers() = listOf(
            jcacheManager<CaffeineCachingProvider>(),
            jcacheManager<EhcacheCachingProvider>(),
            // FIXME: Cache2k CacheManager를 사용하면, 자동으로 Close 되어버린다 ???
            // jcacheManager<org.cache2k.jcache.provider.JCacheProvider>()
        ).map { Arguments.of(it) }
    }

    @Test
    fun `load caffeine jcache manager`() {
        jcacheManager<CaffeineCachingProvider>().shouldNotBeNull()
        jcacheManager<EhcacheCachingProvider>().shouldNotBeNull()
        jcacheManager<org.cache2k.jcache.provider.JCacheProvider>().shouldNotBeNull()
    }

    @ParameterizedTest(name = "jcache manager={0}")
    @MethodSource("getJCacheManagers")
    fun `use jcache`(manager: CacheManager) {
        val cache = manager.getOrCreate<String, Any>("jcache")
        cache.shouldNotBeNull()

        cache.putIfAbsent("first-put", 0L).shouldBeTrue()
        cache.putIfAbsent("first-put", 1L).shouldBeFalse()

        cache.getOrPut("first-put") { 2L } shouldBeEqualTo 0L

        cache.getOrPut("second-put") { 3L } shouldBeEqualTo 3L

        cache["first-put"] shouldBeEqualTo 0L
        cache["second-put"] shouldBeEqualTo 3L
        cache["not-exists"].shouldBeNull()
    }
}
