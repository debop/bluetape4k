package io.bluetape4k.infra.cache.memorizer.caffeine

import io.bluetape4k.infra.cache.caffeine.cache
import io.bluetape4k.infra.cache.memorizer.AbstractAsyncMemorizerTest
import io.bluetape4k.infra.cache.memorizer.AsyncFactorialProvider
import io.bluetape4k.infra.cache.memorizer.AsyncFibonacciProvider
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ForkJoinPool

class AsyncCaffeineMemorizerTest: AbstractAsyncMemorizerTest() {

    override val factorial: AsyncFactorialProvider = AsyncCaffeineFactorialProvider()
    override val fibonacci: AsyncFibonacciProvider = AsyncCaffeineFibonacciProvider()

    private val caffeine = io.bluetape4k.infra.cache.caffeine.Caffeine {
        executor(ForkJoinPool.commonPool())
    }
    val cache = caffeine.cache<Int, Int>()

    override val heavyFunc: (Int) -> CompletableFuture<Int> = cache.asyncMemorizer {
        CompletableFuture.supplyAsync {
            Thread.sleep(100)
            it * it
        }
    }

    private class AsyncCaffeineFactorialProvider: AsyncFactorialProvider() {
        private val caffeine = io.bluetape4k.infra.cache.caffeine.Caffeine {
            executor(ForkJoinPool.commonPool())
        }
        val cache = caffeine.cache<Long, Long>()

        override val cachedCalc: (Long) -> CompletableFuture<Long> by lazy {
            cache.asyncMemorizer { calc(it) }
        }
    }

    private class AsyncCaffeineFibonacciProvider: AsyncFibonacciProvider() {
        private val caffeine = io.bluetape4k.infra.cache.caffeine.Caffeine {
            executor(ForkJoinPool.commonPool())
        }
        val cache = caffeine.cache<Long, Long>()

        override val cachedCalc: (Long) -> CompletableFuture<Long> by lazy {
            cache.asyncMemorizer { calc(it) }
        }
    }

}
