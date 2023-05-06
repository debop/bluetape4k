package io.bluetape4k.infra.cache.memorizer.caffeine

import io.bluetape4k.infra.cache.caffeine.cache
import io.bluetape4k.infra.cache.memorizer.AbstractMemorizerTest
import io.bluetape4k.infra.cache.memorizer.FactorialProvider
import io.bluetape4k.infra.cache.memorizer.FibonacciProvider
import java.util.concurrent.ForkJoinPool

class CaffeineMemorizerTest: AbstractMemorizerTest() {

    override val factorial: FactorialProvider = CaffeineFactorialProvider()
    override val fibonacci: FibonacciProvider = CaffeineFibonacciProvider()

    private val caffeine = io.bluetape4k.infra.cache.caffeine.caffeine {
        executor(ForkJoinPool.commonPool())
    }

    val cache = caffeine.cache<Int, Int>()

    override val heavyFunc: (Int) -> Int = cache.memorizer {
        Thread.sleep(100)
        it * it
    }

    private class CaffeineFactorialProvider: FactorialProvider() {
        val caffeine = io.bluetape4k.infra.cache.caffeine.caffeine {
            executor(ForkJoinPool.commonPool())
        }
        val cache = caffeine.cache<Long, Long>()

        override val cachedCalc: (Long) -> Long by lazy {
            cache.memorizer { calc(it) }
        }
    }

    private class CaffeineFibonacciProvider: FibonacciProvider() {
        val caffeine = io.bluetape4k.infra.cache.caffeine.caffeine {
            executor(ForkJoinPool.commonPool())
        }
        val cache = caffeine.cache<Long, Long>()

        override val cachedCalc: (Long) -> Long by lazy {
            cache.memorizer { calc(it) }
        }
    }
}
