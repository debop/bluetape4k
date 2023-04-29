package io.bluetape4k.infra.resilience4j.bulkhead

import io.github.resilience4j.bulkhead.Bulkhead
import io.github.resilience4j.bulkhead.BulkheadFullException
import io.github.resilience4j.core.functions.CheckedConsumer
import io.github.resilience4j.core.functions.CheckedRunnable
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage
import java.util.function.Consumer

fun Bulkhead.runnable(runnable: () -> Unit): () -> Unit = {
    Bulkhead.decorateRunnable(this) { runnable() }.run()
}

fun Bulkhead.checkedRunnable(runnable: () -> Unit): CheckedRunnable =
    Bulkhead.decorateCheckedRunnable(this) { runnable() }

fun <T> Bulkhead.callable(callable: () -> T): () -> T = {
    Bulkhead.decorateCallable(this) { callable() }.call()
}

fun <T> Bulkhead.supplier(supplier: () -> T): () -> T = {
    Bulkhead.decorateSupplier(this) { supplier() }.get()
}

fun <T> Bulkhead.checkedSupplier(supplier: () -> T): () -> T = {
    Bulkhead.decorateCheckedSupplier(this) { supplier() }.get()
}

fun <T> Bulkhead.consumer(consumer: (T) -> Unit): (T) -> Unit = { input: T ->
    Bulkhead.decorateConsumer(this, Consumer<T> { consumer(it) }).accept(input)
}

fun <T> Bulkhead.checkedConsumer(consumer: (T) -> Unit): CheckedConsumer<T> =
    Bulkhead.decorateCheckedConsumer(this) { consumer(it) }

fun <T, R> Bulkhead.function(func: (T) -> R): (T) -> R = { input ->
    Bulkhead.decorateFunction<T, R>(this) { func(it) }.apply(input)
}

fun <T, R> Bulkhead.checkedFunction(func: (T) -> R): (T) -> R = { input ->
    Bulkhead.decorateCheckedFunction<T, R>(this) { func(it) }.apply(input)
}

//
// 비동기 방식 함수
//

fun <T> Bulkhead.completionStage(supplier: () -> CompletionStage<T>): () -> CompletionStage<T> = {
    Bulkhead.decorateCompletionStage(this) { supplier() }.get()
}

fun <T, R> Bulkhead.completableFuture(
    func: (T) -> CompletableFuture<R>,
): (T) -> CompletableFuture<R> {
    return decorateCompletableFuture(func)
}


fun <T, R> Bulkhead.decorateCompletableFuture(
    func: (T) -> CompletableFuture<R>,
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
