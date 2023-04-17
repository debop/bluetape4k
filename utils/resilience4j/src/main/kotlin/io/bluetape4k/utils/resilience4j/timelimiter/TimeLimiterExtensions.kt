package io.bluetape4k.utils.resilience4j.timelimiter

import io.github.resilience4j.timelimiter.TimeLimiter
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.ScheduledExecutorService

fun <T, F: Future<T>> TimeLimiter.futureSupplier(futureSupplier: () -> F): () -> T = {
    TimeLimiter.decorateFutureSupplier(this) { futureSupplier.invoke() }.call()
}

fun <T, F: CompletionStage<T>> TimeLimiter.completionStage(
    scheduler: ScheduledExecutorService,
    futureSupplier: () -> F,
): () -> T = {
    TimeLimiter
        .decorateCompletionStage(this, scheduler) { futureSupplier.invoke() }
        .get()
        .toCompletableFuture()
        .get()
}


@Suppress("UNCHECKED_CAST")
fun <T, R: CompletableFuture<T>> TimeLimiter.completableFuture(
    schedulre: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor(),
    func: (T) -> R,
): (T) -> R =
    decorateCompletableFuture(schedulre, func)

@Suppress("UNCHECKED_CAST")
fun <T, R: CompletableFuture<T>> TimeLimiter.decorateCompletableFuture(
    schedulre: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor(),
    func: (T) -> R,
): (T) -> R = { input: T ->
    this.executeCompletionStage<T, R>(schedulre) { func.invoke(input) }.toCompletableFuture() as R
}
