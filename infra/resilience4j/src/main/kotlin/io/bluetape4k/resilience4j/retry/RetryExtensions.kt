package io.bluetape4k.resilience4j.retry

import io.github.resilience4j.core.functions.CheckedRunnable
import io.github.resilience4j.retry.Retry
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService


inline fun Retry.runnable(
    crossinline runnable: () -> Unit,
): Runnable {
    return Retry.decorateRunnable(this) { runnable() }
}

inline fun Retry.checkedRunnable(
    crossinline runnable: () -> Unit,
): CheckedRunnable {
    return Retry.decorateCheckedRunnable(this) { runnable() }
}

inline fun <T> Retry.callable(
    crossinline callable: () -> T,
): () -> T = {
    Retry.decorateCallable(this) { callable() }.call()
}

inline fun <T> Retry.supplier(
    crossinline supplier: () -> T,
): () -> T = {
    Retry.decorateSupplier(this) { supplier() }.get()
}

inline fun <T> Retry.checkedSupplier(
    crossinline supplier: () -> T,
): () -> T = {
    Retry.decorateCheckedSupplier(this) { supplier() }.get()
}

inline fun <T, R> Retry.function(
    crossinline func: (T) -> R,
): (T) -> R = { input ->
    Retry.decorateFunction<T, R>(this) { func(it) }.apply(input)
}

inline fun <T, R> Retry.checkedFunction(
    crossinline func: (T) -> R,
): (T) -> R = { input ->
    Retry.decorateCheckedFunction<T, R>(this) { func(it) }.apply(input)
}

//
// 비동기 방식
//

/**
 * Decorates CompletionStage Supplier with Retry
 *
 * @param scheduler execution service to use to schedule retries
 * @param supplier  completions stage supplier
 * @param <T>       type of completions stage result
 * @return decorated supplier
 */
inline fun <T> Retry.completionStage(
    scheduler: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor(),
    crossinline supplier: () -> CompletionStage<T>,
): () -> CompletionStage<T> = {
    Retry.decorateCompletionStage(this, scheduler) { supplier() }
        .get()
        .whenComplete { _, _ -> scheduler.shutdown() }

}

inline fun <T, R> withRetry(
    retry: Retry,
    scheduler: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor(),
    crossinline supplier: (T) -> CompletableFuture<R>,
): (T) -> CompletableFuture<R> = { input: T ->
    Retry.decorateCompletionStage(retry, scheduler) { supplier.invoke(input) }
        .get()
        .toCompletableFuture()
        .whenComplete { _, _ -> scheduler.shutdown() }
}

inline fun <T, R> Retry.completableFutureFunction(
    scheduler: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor(),
    crossinline func: (T) -> CompletableFuture<R>,
): (T) -> CompletableFuture<R> {
    return completableFuture(scheduler, func)
}

inline fun <T, R> Retry.completableFuture(
    scheduler: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor(),
    crossinline func: (T) -> CompletableFuture<R>,
): (T) -> CompletableFuture<R> = { input: T ->
    this.executeCompletionStage<R>(scheduler) { func.invoke(input) }
        .toCompletableFuture()
        .whenComplete { _, _ -> scheduler.shutdown() }
}
