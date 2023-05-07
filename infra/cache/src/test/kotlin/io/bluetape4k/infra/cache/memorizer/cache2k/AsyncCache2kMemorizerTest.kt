package io.bluetape4k.infra.cache.memorizer.cache2k

import io.bluetape4k.infra.cache.cache2k.cache2k
import io.bluetape4k.infra.cache.memorizer.AbstractAsyncMemorizerTest
import io.bluetape4k.infra.cache.memorizer.AsyncFactorialProvider
import io.bluetape4k.infra.cache.memorizer.AsyncFibonacciProvider
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ForkJoinPool

class AsyncCache2kMemorizerTest: AbstractAsyncMemorizerTest() {

    override val factorial: AsyncFactorialProvider = AsyncCache2kFactorialProvider()
    override val fibonacci: AsyncFibonacciProvider = AsyncCache2kFibonacciProvider()

    val cache = cache2k<Int, Int> {
        this.executor(ForkJoinPool.commonPool())
    }.build()

    override val heavyFunc: (Int) -> CompletableFuture<Int> = cache.asyncMemorizer {
        CompletableFuture.supplyAsync {
            Thread.sleep(100)
            it * it
        }
    }

    private class AsyncCache2kFactorialProvider: AsyncFactorialProvider() {
        val cache = cache2k<Long, Long> {
            this.executor(ForkJoinPool.commonPool())
        }.build()

        override val cachedCalc: (Long) -> CompletableFuture<Long> by lazy {
            cache.asyncMemorizer { calc(it) }
        }
    }

    private class AsyncCache2kFibonacciProvider: AsyncFibonacciProvider() {
        val cache = cache2k<Long, Long> {
            this.executor(ForkJoinPool.commonPool())
        }.build()

        override val cachedCalc: (Long) -> CompletableFuture<Long> by lazy {
            cache.asyncMemorizer { calc(it) }
        }
    }
}
