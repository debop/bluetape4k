package io.bluetape4k.utils.cache.memorizer.inmemory

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.trace
import io.bluetape4k.utils.cache.memorizer.AbstractMemorizerTest
import io.bluetape4k.utils.cache.memorizer.FactorialProvider
import io.bluetape4k.utils.cache.memorizer.FibonacciProvider

class InMemoryMemorizerTest: AbstractMemorizerTest() {

    companion object: KLogging()

    override val factorial: FactorialProvider = InMemoryFactorialProvider()

    override val fibonacci: FibonacciProvider = InMemoryFibonacciProvider()

    override val heavyFunc: (Int) -> Int = InMemoryMemorizer { x ->
        log.trace { "heavy($x)" }
        Thread.sleep(100)
        x * x
    }


    private class InMemoryFibonacciProvider: FibonacciProvider() {
        override val cachedCalc: (Long) -> Long by lazy { InMemoryMemorizer { calc(it) } }
    }

    private class InMemoryFactorialProvider: FactorialProvider() {
        override val cachedCalc: (Long) -> Long by lazy { InMemoryMemorizer { calc(it) } }
    }
}
