package io.bluetape4k.utils.cache.memorizer.inmemory

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.trace
import io.bluetape4k.utils.cache.memorizer.AbstractAsyncMemorizerTest
import io.bluetape4k.utils.cache.memorizer.AsyncFactorialProvider
import io.bluetape4k.utils.cache.memorizer.AsyncFibonacciProvider
import java.util.concurrent.CompletableFuture

class AsyncInMemoryMemorizerTest: AbstractAsyncMemorizerTest() {

    companion object: KLogging()

    override val factorial: AsyncFactorialProvider = InMemoryAsyncFactorialProvider()

    override val fibonacci: AsyncFibonacciProvider = InMemoryAsyncFibonacciProvider()

    override val heavyFunc: (Int) -> CompletableFuture<Int> = InMemoryMemorizer { x ->
        CompletableFuture.supplyAsync {
            log.trace { "heavy($x)" }

            Thread.sleep(100)
            x * x
        }
    }

    private class InMemoryAsyncFactorialProvider: AsyncFactorialProvider() {
        override val cachedCalc: (Long) -> CompletableFuture<Long> by lazy {
            AsyncInMemoryMemorizer { calc(it) }
        }
    }

    private class InMemoryAsyncFibonacciProvider: AsyncFibonacciProvider() {
        override val cachedCalc: (Long) -> CompletableFuture<Long> by lazy {
            AsyncInMemoryMemorizer { calc(it) }
        }
    }
}
