package io.bluetape4k.infra.cache.jcache

import com.github.benmanes.caffeine.jcache.spi.CaffeineCachingProvider
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeFalse
import org.amshove.kluent.shouldBeNull
import org.amshove.kluent.shouldBeTrue
import org.amshove.kluent.shouldNotBeNull
import org.ehcache.jsr107.EhcacheCachingProvider
import org.junit.jupiter.api.Test

class JCacheSupportTest {

    @Test
    fun `load caffeine jcache manager`() {
        jcacheManager<CaffeineCachingProvider>().shouldNotBeNull()
        jcacheManager<EhcacheCachingProvider>().shouldNotBeNull()
    }

    @Test
    fun `get or create jcache`() {
        val manager = jcacheManager<CaffeineCachingProvider>()
        val cache = manager.getOrCreate<String, Any>("caffeine-jcache")
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
