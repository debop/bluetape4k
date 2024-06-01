package io.bluetape4k.resilience4j.circuitbreaker

import io.github.resilience4j.circuitbreaker.CallNotPermittedException
import io.github.resilience4j.circuitbreaker.CircuitBreaker
import io.github.resilience4j.core.functions.CheckedConsumer
import io.github.resilience4j.core.functions.CheckedRunnable
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage
import java.util.concurrent.TimeUnit

inline fun CircuitBreaker.runnable(crossinline runnable: () -> Unit): () -> Unit = {
    CircuitBreaker.decorateRunnable(this) { runnable() }.run()
}

inline fun CircuitBreaker.checkedRunnable(crossinline runnable: () -> Unit): CheckedRunnable =
    CircuitBreaker.decorateCheckedRunnable(this) { runnable() }

inline fun <T> CircuitBreaker.callable(
    crossinline callable: () -> T,
): () -> T = {
    CircuitBreaker.decorateCallable(this) { callable() }.call()
}

inline fun <T> CircuitBreaker.supplier(
    crossinline supplier: () -> T,
): () -> T = {
    CircuitBreaker.decorateSupplier(this) { supplier() }.get()
}

inline fun <T> CircuitBreaker.checkedSupplier(
    crossinline supplier: () -> T,
): () -> T = {
    CircuitBreaker.decorateCheckedSupplier(this) { supplier() }.get()
}

inline fun <T> CircuitBreaker.consumer(
    crossinline consumer: (T) -> Unit,
): (T) -> Unit = { input: T ->
    CircuitBreaker.decorateConsumer<T>(this) { consumer(it) }.accept(input)
}

inline fun <T> CircuitBreaker.checkedConsumer(
    crossinline consumer: (T) -> Unit,
): CheckedConsumer<T> {
    return CircuitBreaker.decorateCheckedConsumer(this) { consumer(it) }
}


inline fun <T, R> CircuitBreaker.function(
    crossinline func: (T) -> R,
): (T) -> R = { input ->
    CircuitBreaker.decorateFunction<T, R>(this) { func(it) }.apply(input)
}

inline fun <T, R> CircuitBreaker.checkedFunction(
    crossinline func: (T) -> R,
): (T) -> R = { input ->
    CircuitBreaker.decorateCheckedFunction<T, R>(this) { func(it) }.apply(input)
}

//
// 비동기 방식
//

inline fun <T> CircuitBreaker.completionStatge(
    crossinline supplier: () -> CompletionStage<T>,
): () -> CompletionStage<T> = {
    CircuitBreaker.decorateCompletionStage(this) { supplier() }.get()
}

inline fun <T, R> CircuitBreaker.completableFuture(
    crossinline func: (T) -> CompletableFuture<R>,
): (T) -> CompletableFuture<R> {
    return decorateCompletableFuture(func)
}

inline fun <T, R> CircuitBreaker.decorateCompletableFuture(
    crossinline func: (T) -> CompletableFuture<R>,
): (T) -> CompletableFuture<R> = { input: T ->

    val promise = CompletableFuture<R>()

    if (!tryAcquirePermission()) {
        promise.completeExceptionally(CallNotPermittedException.createCallNotPermittedException(this))
    } else {
        val start = System.nanoTime()
        try {
            func(input)
                .whenComplete { result, error ->
                    val durationInNanos = System.nanoTime() - start
                    if (error != null) {
                        if (error is Exception) {
                            onError(durationInNanos, TimeUnit.NANOSECONDS, error)
                        }
                        promise.completeExceptionally(error)
                    } else {
                        onSuccess(durationInNanos, TimeUnit.NANOSECONDS)
                        promise.complete(result)
                    }
                }
        } catch (e: Throwable) {
            val durationInNanos = System.nanoTime() - start
            onError(durationInNanos, TimeUnit.NANOSECONDS, e)
            promise.completeExceptionally(e)
        }
    }

    promise
}
