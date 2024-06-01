package io.bluetape4k.resilience4j.ratelimiter

import io.github.resilience4j.core.functions.CheckedRunnable
import io.github.resilience4j.ratelimiter.RateLimiter
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage
import java.util.function.Consumer

inline fun RateLimiter.runnable(
    crossinline runnable: () -> Unit,
): () -> Unit = {
    RateLimiter.decorateRunnable(this) { runnable() }.run()
}

inline fun RateLimiter.checkedRunnable(
    crossinline runnable: () -> Unit,
): CheckedRunnable =
    RateLimiter.decorateCheckedRunnable(this) { runnable() }

inline fun <T> RateLimiter.callable(
    crossinline callable: () -> T,
): () -> T = {
    RateLimiter.decorateCallable(this) { callable() }.call()
}

inline fun <T> RateLimiter.supplier(
    crossinline supplier: () -> T,
): () -> T = {
    RateLimiter.decorateSupplier(this) { supplier() }.get()
}

inline fun <T> RateLimiter.checkedSupplier(
    crossinline supplier: () -> T,
): () -> T = {
    RateLimiter.decorateCheckedSupplier(this) { supplier() }.get()
}

inline fun <T> RateLimiter.consumer(
    crossinline consumer: (T) -> Unit,
): (T) -> Unit = { input: T ->
    RateLimiter.decorateConsumer(this, Consumer<T> { consumer(it) }).accept(input)
}

inline fun <T, R> RateLimiter.function(
    crossinline func: (T) -> R,
): (T) -> R = { input: T ->
    RateLimiter.decorateFunction<T, R>(this) { func(it) }.apply(input)
}

inline fun <T, R> RateLimiter.checkedFunction(
    crossinline func: (T) -> R,
): (T) -> R = { input: T ->
    RateLimiter.decorateCheckedFunction<T, R>(this) { func(it) }.apply(input)
}

//
// 비동기 방식
//

/**
 * Decorates CompletionStage Supplier with Retry
 *
 * @param supplier  completions stage supplier
 * @param <T>       type of completions stage result
 * @return decorated supplier
 */
inline fun <T> RateLimiter.completionStage(
    crossinline supplier: () -> CompletionStage<T>,
): () -> CompletionStage<T> = {
    RateLimiter.decorateCompletionStage(this) { supplier() }.get()
}

inline fun <T, R> RateLimiter.completableFuture(
    crossinline func: (T) -> CompletableFuture<R>,
): (T) -> CompletableFuture<R> =
    decorateCompletableFuture(func)

inline fun <T, R> RateLimiter.decorateCompletableFuture(
    crossinline func: (T) -> CompletableFuture<R>,
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
    } catch (e: Throwable) {
        promise.completeExceptionally(e)
    }

    promise
}
