package io.bluetape4k.infra.cache.memorizer

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.trace
import java.util.concurrent.CompletableFuture

abstract class AsyncFibonacciProvider {

    companion object: KLogging()

    abstract val cachedCalc: (Long) -> CompletableFuture<Long>

    fun calc(x: Long): CompletableFuture<Long> {
        log.trace { "factorial($x)" }
        return when {
            x <= 0L -> CompletableFuture.completedFuture(0L)
            x <= 2L -> CompletableFuture.completedFuture(1L)
            else    -> cachedCalc(x - 1)
                .thenComposeAsync { x1 ->
                    cachedCalc(x - 2).thenApplyAsync { x2 -> x1 + x2 }
                }
        }
    }
}
