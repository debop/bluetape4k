package io.bluetape4k.vertx.resilience4j

import io.github.resilience4j.retry.Retry
import io.vertx.core.Future
import io.vertx.core.Promise
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService

inline fun <T> Retry.executeVertxFuture(
    scheduler: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor(),
    crossinline supplier: () -> Future<T>,
): Future<T> {
    return decorateVertxFuture(scheduler, supplier).invoke()
}

inline fun <T> Retry.decorateVertxFuture(
    scheduler: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor(),
    crossinline supplier: () -> Future<T>,
): () -> Future<T> = {
    val promise = Promise.promise<T>()

    Retry.decorateCompletionStage(this, scheduler) { supplier().toCompletionStage() }
        .get()
        .whenComplete { result, cause ->
            if (cause != null) promise.fail(cause)
            else promise.complete(result)
        }

    promise.future()
}
