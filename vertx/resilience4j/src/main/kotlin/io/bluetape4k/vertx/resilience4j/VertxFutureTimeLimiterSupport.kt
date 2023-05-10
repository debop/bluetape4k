package io.bluetape4k.vertx.resilience4j

import io.github.resilience4j.timelimiter.TimeLimiter
import io.vertx.core.Future
import io.vertx.core.Promise
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService

inline fun <T, F: Future<T>> TimeLimiter.executeVertxFuture(
    scheduler: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor(),
    crossinline supplier: () -> F,
): Future<T> {
    return decorateVertxFuture(scheduler, supplier).invoke()
}

inline fun <T, F: Future<T>> TimeLimiter.decorateVertxFuture(
    scheduler: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor(),
    crossinline supplier: () -> F,
): () -> Future<T> = {
    val promise = Promise.promise<T>()

    decorateCompletionStage(scheduler) { supplier().toCompletionStage() }
        .get()
        .whenComplete { result, cause ->
            if (cause != null) promise.fail(cause)
            else promise.complete(result)
        }

    promise.future()
}
