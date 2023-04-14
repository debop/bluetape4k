package io.bluetape4k.utils.cache.memorizer.ehcache

import io.bluetape4k.utils.cache.ehcache.ehcacheManager
import io.bluetape4k.utils.cache.ehcache.getOrCreateCache
import io.bluetape4k.utils.cache.memorizer.AbstractAsyncMemorizerTest
import io.bluetape4k.utils.cache.memorizer.AsyncFactorialProvider
import io.bluetape4k.utils.cache.memorizer.AsyncFibonacciProvider
import java.util.concurrent.CompletableFuture

class AsyncEhcachememorizerTest: AbstractAsyncMemorizerTest() {

    override val factorial: AsyncFactorialProvider = AsyncEhcacheFactorialProvider()
    override val fibonacci: AsyncFibonacciProvider = AsyncEhcacheFibonacciProvider()

    private val ehcacheManager = ehcacheManager {}

    val cache = ehcacheManager.getOrCreateCache<Int, Int>("heavy")

    override val heavyFunc: (Int) -> CompletableFuture<Int> = cache.asyncMemorizer {
        CompletableFuture.supplyAsync {
            Thread.sleep(100)
            it * it
        }
    }

    private class AsyncEhcacheFactorialProvider: AsyncFactorialProvider() {
        private val ehcacheManager = ehcacheManager {}
        val cache = ehcacheManager.getOrCreateCache<Long, Long>("factorial")

        override val cachedCalc: (Long) -> CompletableFuture<Long> by lazy {
            cache.asyncMemorizer { calc(it) }
        }
    }

    private class AsyncEhcacheFibonacciProvider: AsyncFibonacciProvider() {
        private val ehcacheManager = ehcacheManager {}
        val cache = ehcacheManager.getOrCreateCache<Long, Long>("fibonacci")

        override val cachedCalc: (Long) -> CompletableFuture<Long> by lazy {
            cache.asyncMemorizer { calc(it) }
        }
    }
}
