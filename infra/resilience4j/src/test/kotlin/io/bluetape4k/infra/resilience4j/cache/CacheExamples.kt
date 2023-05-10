package io.bluetape4k.infra.resilience4j.cache

import io.bluetape4k.codec.encodeBase62
import io.bluetape4k.infra.cache.jcache.JCaching
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.logging.warn
import io.github.resilience4j.cache.Cache
import io.github.resilience4j.decorators.Decorators
import kotlinx.atomicfu.atomic
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class CacheExamples {

    companion object: KLogging()

    private val jcache: javax.cache.Cache<String, String> by lazy {
        JCaching.Caffeine.getOrCreate("jcache-" + UUID.randomUUID().encodeBase62())
    }
    private val cache: Cache<String, String> = Cache.of(jcache)

    @BeforeEach
    fun setup() {
        jcache.clear()
    }

    @Test
    fun `setup resilience4j cache with caffein jcache`() {
        cache.eventPublisher.onEvent { log.debug { "onEvent=$it" } }
        cache.eventPublisher.onError { log.warn(it.throwable) { "OnError. Event=${it}" } }

        val callCounter = atomic(0)
        val called by callCounter

        val function: () -> String = {
            callCounter.incrementAndGet()
            "Do something"
        }

        val cachedFunction = Decorators
            .ofSupplier(function)
            .withCache(cache)
            .decorate()

        cachedFunction.apply("cacheKey") shouldBeEqualTo "Do something"
        cachedFunction.apply("cacheKey") shouldBeEqualTo "Do something"

        called shouldBeEqualTo 1
        cache.metrics.numberOfCacheHits shouldBeEqualTo 1
        cache.metrics.numberOfCacheMisses shouldBeEqualTo 1
    }
}
