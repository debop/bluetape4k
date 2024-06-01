package io.bluetape4k.resilience4j.timelimiter

import io.github.resilience4j.timelimiter.TimeLimiter
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.ScheduledExecutorService

inline fun <T, F: Future<T>> TimeLimiter.futureSupplier(
    crossinline futureSupplier: () -> F,
): () -> T = {
    TimeLimiter.decorateFutureSupplier(this) { futureSupplier.invoke() }.call()
}

inline fun <T, F: CompletionStage<T>> TimeLimiter.completionStage(
    scheduler: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor(),
    crossinline futureSupplier: () -> F,
): () -> T = {
    TimeLimiter
        .decorateCompletionStage(this, scheduler) { futureSupplier.invoke() }
        .get()
        .whenComplete { _, _ -> scheduler.shutdown() }
        .toCompletableFuture()
        .get()
}

inline fun <T, R: CompletableFuture<T>> TimeLimiter.completableFuture(
    scheduler: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor(),
    crossinline func: (T) -> R,
): (T) -> R {
    return decorateCompletableFuture(scheduler, func)
}

@Suppress("UNCHECKED_CAST")
inline fun <T, R: CompletableFuture<T>> TimeLimiter.decorateCompletableFuture(
    scheduler: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor(),
    crossinline func: (T) -> R,
): (T) -> R = { input: T ->
    this.executeCompletionStage<T, R>(scheduler) { func(input) }
        .toCompletableFuture()
        .whenComplete { _, _ -> scheduler.shutdown() }
            as R
}
