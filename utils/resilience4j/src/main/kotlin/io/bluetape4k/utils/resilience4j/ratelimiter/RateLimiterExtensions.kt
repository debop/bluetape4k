package io.bluetape4k.utils.resilience4j.ratelimiter

import io.github.resilience4j.core.functions.CheckedRunnable
import io.github.resilience4j.ratelimiter.RateLimiter
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage
import java.util.function.Consumer

inline fun RateLimiter.runnable(crossinline runnable: () -> Unit): () -> Unit = {
    RateLimiter.decorateRunnable(this) { runnable() }.run()
}

inline fun RateLimiter.checkedRunnable(crossinline runnable: () -> Unit): CheckedRunnable =
    RateLimiter.decorateCheckedRunnable(this) { runnable() }

fun <T> RateLimiter.callable(callable: () -> T): () -> T = {
    RateLimiter.decorateCallable(this) { callable() }.call()
}

fun <T> RateLimiter.supplier(supplier: () -> T): () -> T = {
    RateLimiter.decorateSupplier(this) { supplier() }.get()
}

fun <T> RateLimiter.checkedSupplier(supplier: () -> T): () -> T = {
    RateLimiter.decorateCheckedSupplier(this) { supplier() }.get()
}

fun <T> RateLimiter.consumer(consumer: (T) -> Unit): (T) -> Unit = { input: T ->
    RateLimiter.decorateConsumer(this, Consumer<T> { consumer(it) }).accept(input)
}

fun <T, R> RateLimiter.function(func: (T) -> R): (T) -> R = { input: T ->
    RateLimiter.decorateFunction<T, R>(this) { func(it) }.apply(input)
}

fun <T, R> RateLimiter.checkedFunction(func: (T) -> R): (T) -> R = { input: T ->
    RateLimiter.decorateCheckedFunction<T, R>(this) { func(it) }.apply(input)
}

//
// 비동기 방식
//

/**
 * Decorates CompletionStage Supplier with Retry
 *
 * @param supplier  completion stage supplier
 * @param <T>       type of completion stage result
 * @return decorated supplier
 */
fun <T> RateLimiter.completionStage(supplier: () -> CompletionStage<T>): () -> CompletionStage<T> = {
    RateLimiter.decorateCompletionStage(this) { supplier() }.get()
}

fun <T, R> RateLimiter.completableFuture(func: (T) -> CompletableFuture<R>): (T) -> CompletableFuture<R> =
    decorateCompletableFuture(func)

fun <T, R> RateLimiter.decorateCompletableFuture(
    func: (T) -> CompletableFuture<R>,
): (T) -> CompletableFuture<R> = { input: T ->
    val promise = CompletableFuture<R>()
    try {
        RateLimiter.waitForPermission(this)
        func(input)
            .whenComplete { result, error ->
                when (error) {
                    null -> promise.complete(result)
                    else -> promise.completeExceptionally(error)
                }
            }
    } catch (e: Exception) {
        promise.completeExceptionally(e)
    }

    promise
}
