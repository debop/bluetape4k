package io.bluetape4k.utils.resilience4j.cache

import io.bluetape4k.logging.KLogging
import io.github.resilience4j.cache.Cache
import io.github.resilience4j.decorators.Decorators
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CacheExamples {

    companion object: KLogging()

    private val jcache = CaffeineJCacheProvider.getJCache<String, String>("examples")
    private val cache = Cache.of(jcache)

    @BeforeEach
    fun setup() {
        jcache.clear()
    }

    @Test
    fun `setup resilience4j cache with caffeine`() {
        jcache.clear()
        var hits = 0
        var missed = 0

        cache.eventPublisher
            .onCacheHit { hits++ }
            .onCacheMiss { missed++ }

        var called = 0
        val function: () -> String = {
            called++
            "Do something"
        }

        val cachedFunction = Decorators
            .ofSupplier(function)
            .withCache(cache)
            .decorate()

        cachedFunction.apply("cacheKey") shouldBeEqualTo "Do something"
        cachedFunction.apply("cacheKey") shouldBeEqualTo "Do something"

        hits shouldBeEqualTo 1
        missed shouldBeEqualTo 1
        called shouldBeEqualTo 1
    }
}
