package io.bluetape4k.utils.resilience4j.circuitbreaker

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

fun <T> CircuitBreaker.callable(callable: () -> T): () -> T = {
    CircuitBreaker.decorateCallable(this) { callable() }.call()
}

fun <T> CircuitBreaker.supplier(supplier: () -> T): () -> T = {
    CircuitBreaker.decorateSupplier(this) { supplier() }.get()
}

fun <T> CircuitBreaker.checkedSupplier(supplier: () -> T): () -> T = {
    CircuitBreaker.decorateCheckedSupplier(this) { supplier() }.get()
}

fun <T> CircuitBreaker.consumer(consumer: (T) -> Unit): (T) -> Unit = { input: T ->
    CircuitBreaker.decorateConsumer<T>(this) { consumer(it) }.accept(input)
}

fun <T> CircuitBreaker.checkedConsumer(consumer: (T) -> Unit): CheckedConsumer<T> =
    CircuitBreaker.decorateCheckedConsumer(this) { consumer(it) }


fun <T, R> CircuitBreaker.function(func: (T) -> R): (T) -> R = { input ->
    CircuitBreaker.decorateFunction<T, R>(this) { func(it) }.apply(input)
}

fun <T, R> CircuitBreaker.checkedFunction(func: (T) -> R): (T) -> R = { input ->
    CircuitBreaker.decorateCheckedFunction<T, R>(this) { func(it) }.apply(input)
}

//
// 비동기 방식
//

fun <T> CircuitBreaker.completionStatge(supplier: () -> CompletionStage<T>): () -> CompletionStage<T> = {
    CircuitBreaker.decorateCompletionStage(this) { supplier() }.get()
}

fun <T, R> CircuitBreaker.completableFuture(func: (T) -> CompletableFuture<R>): (T) -> CompletableFuture<R> =
    decorateCompletableFuture(func)

fun <T, R> CircuitBreaker.decorateCompletableFuture(
    func: (T) -> CompletableFuture<R>,
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
        } catch (e: Exception) {
            val durationInNanos = System.nanoTime() - start
            onError(durationInNanos, TimeUnit.NANOSECONDS, e)
            promise.completeExceptionally(e)
        }
    }

    promise
}
