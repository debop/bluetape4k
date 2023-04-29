package io.bluetape4k.infra.resilience4j.retry

import io.github.resilience4j.core.functions.CheckedRunnable
import io.github.resilience4j.retry.Retry
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService


inline fun Retry.runnable(crossinline runnable: () -> Unit): Runnable =
    Retry.decorateRunnable(this) { runnable() }

inline fun Retry.checkedRunnable(crossinline runnable: () -> Unit): CheckedRunnable =
    Retry.decorateCheckedRunnable(this) { runnable() }

fun <T> Retry.callable(callable: () -> T): () -> T = {
    Retry.decorateCallable(this) { callable() }.call()
}

fun <T> Retry.supplier(supplier: () -> T): () -> T = {
    Retry.decorateSupplier(this) { supplier() }.get()
}

fun <T> Retry.checkedSupplier(supplier: () -> T): () -> T = {
    Retry.decorateCheckedSupplier(this) { supplier() }.get()
}

fun <T, R> Retry.function(func: (T) -> R): (T) -> R = { input ->
    Retry.decorateFunction<T, R>(this) { func(it) }.apply(input)
}

fun <T, R> Retry.checkedFunction(func: (T) -> R): (T) -> R = { input ->
    Retry.decorateCheckedFunction<T, R>(this) { func(it) }.apply(input)
}

//
// 비동기 방식
//

/**
 * Decorates CompletionStage Supplier with Retry
 *
 * @param scheduler execution service to use to schedule retries
 * @param supplier  completion stage supplier
 * @param <T>       type of completion stage result
 * @return decorated supplier
 */
fun <T> Retry.completionStage(
    scheduler: ScheduledExecutorService,
    supplier: () -> CompletionStage<T>,
): () -> CompletionStage<T> = {
    Retry.decorateCompletionStage(this, scheduler) { supplier() }.get()
}

fun <T, R> withRetry(
    retry: Retry,
    scheduler: ScheduledExecutorService,
    supplier: (T) -> CompletableFuture<R>,
): (T) -> CompletableFuture<R> = { it ->
    Retry.decorateCompletionStage(retry, scheduler) { supplier.invoke(it) }.get().toCompletableFuture()
}

fun <T, R> Retry.completableFutureFunction(
    scheduler: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor(),
    func: (T) -> CompletableFuture<R>,
): (T) -> CompletableFuture<R> {
    return completableFuture(scheduler, func)
}

fun <T, R> Retry.completableFuture(
    scheduler: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor(),
    func: (T) -> CompletableFuture<R>,
): (T) -> CompletableFuture<R> = { input: T ->
    this.executeCompletionStage<R>(scheduler) { func.invoke(input) }.toCompletableFuture()
}
