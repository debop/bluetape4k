package io.bluetape4k.infra.resilience4j.cache

import io.bluetape4k.concurrent.futureOf
import io.bluetape4k.concurrent.onSuccess
import io.bluetape4k.junit5.coroutines.runSuspendTest
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.logging.error
import io.bluetape4k.logging.trace
import io.github.resilience4j.cache.Cache
import java.util.concurrent.CompletableFuture
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeFalse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

abstract class AbstractJCacheCoroutinesTest {

    companion object: KLogging()

    abstract val jcache: javax.cache.Cache<String, String>
    val cache: Cache<String, String> by lazy { Cache.of(jcache) }

    @BeforeEach
    fun setup() {
        jcache.clear()
    }

    private suspend fun greeting(name: String): String {
        log.debug { "Start populate. name=$name" }
        delay(10)
        log.debug { "Finish populate. name=$name" }
        return "Hi $name!"
    }

    @Test
    fun `decorate suspend function1 for Cache`() = runSuspendTest {
        var hits = 0
        var missed = 0
        cache.eventPublisher
            .onCacheHit { hits++ }
            .onCacheMiss { missed++ }
            .onError { evt -> log.error(evt.throwable) { "Fail to get cache. $evt" } }

        var called = 0
        val function: suspend (String) -> String = { name: String ->
            called++
            greeting(name)
        }

        val cachedFunc = cache.decorateSuspendedFunction(function)

        cachedFunc("debop") shouldBeEqualTo "Hi debop!"
        called shouldBeEqualTo 1

        cachedFunc("Sunghyouk") shouldBeEqualTo "Hi Sunghyouk!"
        called shouldBeEqualTo 2

        cachedFunc("debop") shouldBeEqualTo "Hi debop!"
        called shouldBeEqualTo 2

        cachedFunc("Sunghyouk") shouldBeEqualTo "Hi Sunghyouk!"
        called shouldBeEqualTo 2

        hits shouldBeEqualTo 2
        missed shouldBeEqualTo 4
    }

    @Test
    fun `decorate completableFuture function for Cache`() {
        val callCount = atomic(0L)
        val function: (String) -> CompletableFuture<String> = { name ->
            futureOf {
                log.trace { "Run function ... call count=${callCount.value + 1}" }
                Thread.sleep(100L)
                callCount.incrementAndGet()
                "Hi $name!"
            }
        }

        val cachedFunc = cache.decorateCompletableFutureFunction(function)

        cachedFunc("debop").onSuccess {
            callCount.value shouldBeEqualTo 1L
            it shouldBeEqualTo "Hi debop!"
        }.join()

        cachedFunc("debop").onSuccess {
            callCount.value shouldBeEqualTo 1L
            it shouldBeEqualTo "Hi debop!"
        }.join()

        cachedFunc("Sunghyouk").onSuccess {
            callCount.value shouldBeEqualTo 2L
            it shouldBeEqualTo "Hi Sunghyouk!"
        }.join()

        cachedFunc("Sunghyouk").onSuccess {
            callCount.value shouldBeEqualTo 2L
            it shouldBeEqualTo "Hi Sunghyouk!"
        }.join()
    }

    @Test
    fun `using coroutinesCache`() {
        val coroutinesCache = CoCache.of(jcache)

        var hits = 0
        var missed = 0
        coroutinesCache.eventPublisher
            .onCacheHit { hits++ }
            .onCacheMiss { missed++ }
            .onError { evt -> log.error { "Fail to get cache. $evt" } }

        var called = 0
        val loader: suspend () -> String = {
            called++
            delay(10)
            "Cached"
        }

        coroutinesCache.containsKey("a").shouldBeFalse()
        coroutinesCache.containsKey("b").shouldBeFalse()

        runBlocking {
            coroutinesCache.computeIfAbsent("a", loader) shouldBeEqualTo "Cached"
        }

        runBlocking {
            coroutinesCache.computeIfAbsent("b", loader) shouldBeEqualTo "Cached"
        }

        coroutinesCache.metrics.getNumberOfCacheHits() shouldBeEqualTo 0
        coroutinesCache.metrics.getNumberOfCacheMisses() shouldBeEqualTo 2


        runBlocking {
            coroutinesCache.computeIfAbsent("a", loader) shouldBeEqualTo "Cached"
            coroutinesCache.computeIfAbsent("b", loader) shouldBeEqualTo "Cached"
        }

        coroutinesCache.metrics.getNumberOfCacheHits() shouldBeEqualTo 2
        coroutinesCache.metrics.getNumberOfCacheMisses() shouldBeEqualTo 2
    }
}
