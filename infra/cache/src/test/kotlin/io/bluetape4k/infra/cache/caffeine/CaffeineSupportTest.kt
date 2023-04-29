package io.bluetape4k.infra.cache.caffeine

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import java.time.Duration
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor
import kotlinx.coroutines.delay
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeNull
import org.junit.jupiter.api.Test

class CaffeineSupportTest {

    companion object: KLogging()

    private val caffeine = io.bluetape4k.infra.cache.caffeine.caffeine {
        expireAfterWrite(Duration.ofSeconds(60))
        maximumSize(10_000)
    }

    @Test
    fun `default cache`() {
        val cache = caffeine.cache<String, String>()

        cache.getIfPresent("key").shouldBeNull()

        val value = cache.get("key") {
            Thread.sleep(100)
            "value"
        }
        value shouldBeEqualTo "value"
        cache.put("key", value!!)

        cache.getIfPresent("key")!! shouldBeEqualTo "value"
        cache.invalidate("key")

        cache.getIfPresent("key").shouldBeNull()
    }

    @Test
    fun `loading cache`() {
        val cache = caffeine.loadingCache<String, String> { key ->
            log.debug { "loading value of [$key] ..." }
            Thread.sleep(100)
            "value of $key"
        }

        cache.getIfPresent("key").shouldBeNull()
        cache["key"] shouldBeEqualTo "value of key"
    }

    @Test
    fun `async loading cache`() {
        val asyncCache = caffeine.asyncLoadingCache<String, String> { key ->
            log.debug { "loading cache value of [$key] ..." }
            CompletableFuture.supplyAsync {
                Thread.sleep(100)
                "value of $key"
            }
        }

        asyncCache.getIfPresent("key").shouldBeNull()

        val valueFuture = asyncCache.get("key")

        valueFuture.get() shouldBeEqualTo "value of key"
        asyncCache.put("key", valueFuture)

        asyncCache.getIfPresent("key")!!.get() shouldBeEqualTo "value of key"
        asyncCache.synchronous().invalidate("key")

        asyncCache.getIfPresent("key").shouldBeNull()
    }

    @Test
    fun `async loading cache with executor`() {
        val asyncCache = caffeine.asyncLoadingCache<String, String> { key, _: Executor ->
            log.debug { "loading cache value of [$key] ..." }
            Thread.sleep(100)
            CompletableFuture.completedFuture("value of $key")
        }

        asyncCache.getIfPresent("key").shouldBeNull()

        val valueFuture = asyncCache.get("key")

        valueFuture.get() shouldBeEqualTo "value of key"
        asyncCache.put("key", valueFuture)

        asyncCache.getIfPresent("key")!!.get() shouldBeEqualTo "value of key"
        asyncCache.synchronous().invalidate("key")

        asyncCache.getIfPresent("key").shouldBeNull()
    }

    @Test
    fun `loading async cache by suspend function`() {
        val asyncCache = caffeine.suspendLoadingCache { key: String ->
            log.debug { "loading cache value of [$key] by suspend function..." }
            delay(100)
            "value of $key"
        }

        asyncCache.getIfPresent("key").shouldBeNull()
        asyncCache.get("key").get() shouldBeEqualTo "value of key"

        val valueFuture = asyncCache.get("key")
        valueFuture.get() shouldBeEqualTo "value of key"

        asyncCache.getIfPresent("key")!!.get() shouldBeEqualTo "value of key"
        asyncCache.synchronous().invalidate("key")

        asyncCache.getIfPresent("key").shouldBeNull()
    }

    @Test
    fun `get suspend method with AsyncCache`() {
        val asyncCache = caffeine.asyncCache<String, String>()

        val suspendValue = asyncCache.getSuspend("key") { key ->
            log.debug { "run suspend function to evaluate value of $key" }
            delay(100)
            "suspend value of $key"
        }
        suspendValue.join() shouldBeEqualTo "suspend value of key"
        asyncCache.getIfPresent("key")!!.join() shouldBeEqualTo "suspend value of key"
    }
}
