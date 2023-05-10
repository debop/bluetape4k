package io.bluetape4k.infra.resilience4j.bulkhead

import io.github.resilience4j.bulkhead.Bulkhead
import io.github.resilience4j.bulkhead.BulkheadFullException
import io.github.resilience4j.core.functions.CheckedConsumer
import io.github.resilience4j.core.functions.CheckedRunnable
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage
import java.util.function.Consumer

inline fun Bulkhead.runnable(
    crossinline runnable: () -> Unit,
): () -> Unit = {
    Bulkhead.decorateRunnable(this) { runnable() }.run()
}

inline fun Bulkhead.checkedRunnable(
    crossinline runnable: () -> Unit,
): CheckedRunnable {
    return Bulkhead.decorateCheckedRunnable(this) { runnable() }
}

inline fun <T> Bulkhead.callable(
    crossinline callable: () -> T,
): () -> T = {
    Bulkhead.decorateCallable(this) { callable() }.call()
}

inline fun <T> Bulkhead.supplier(
    crossinline supplier: () -> T,
): () -> T = {
    Bulkhead.decorateSupplier(this) { supplier() }.get()
}

inline fun <T> Bulkhead.checkedSupplier(
    crossinline supplier: () -> T,
): () -> T = {
    Bulkhead.decorateCheckedSupplier(this) { supplier() }.get()
}

inline fun <T> Bulkhead.consumer(
    crossinline consumer: (T) -> Unit,
): (T) -> Unit = { input: T ->
    Bulkhead.decorateConsumer(this, Consumer<T> { consumer(it) }).accept(input)
}

inline fun <T> Bulkhead.checkedConsumer(
    crossinline consumer: (T) -> Unit,
): CheckedConsumer<T> =
    Bulkhead.decorateCheckedConsumer(this) { consumer(it) }

inline fun <T, R> Bulkhead.function(
    crossinline func: (T) -> R,
): (T) -> R = { input ->
    Bulkhead.decorateFunction<T, R>(this) { func(it) }.apply(input)
}

inline fun <T, R> Bulkhead.checkedFunction(
    crossinline func: (T) -> R,
): (T) -> R = { input ->
    Bulkhead.decorateCheckedFunction<T, R>(this) { func(it) }.apply(input)
}

//
// 비동기 방식 함수
//

inline fun <T> Bulkhead.completionStage(
    crossinline supplier: () -> CompletionStage<T>,
): () -> CompletionStage<T> = {
    Bulkhead.decorateCompletionStage(this) { supplier() }.get()
}

inline fun <T, R> Bulkhead.completableFuture(
    crossinline func: (T) -> CompletableFuture<R>,
): (T) -> CompletableFuture<R> {
    return decorateCompletableFuture(func)
}


inline fun <T, R> Bulkhead.decorateCompletableFuture(
    crossinline func: (T) -> CompletableFuture<R>,
): (T) -> CompletableFuture<R> = { input: T ->

    val promise = CompletableFuture<R>()

    if (!tryAcquirePermission()) {
        promise.completeExceptionally(BulkheadFullException.createBulkheadFullException(this))
    } else {
        try {
            func(input)
                .whenComplete { result, error ->
                    onComplete()
                    when (error) {
                        null -> promise.complete(result)
                        else -> promise.completeExceptionally(error)
                    }
                }
        } catch (throwable: Throwable) {
            onComplete()
            promise.completeExceptionally(throwable)
        }
    }

    promise
}
