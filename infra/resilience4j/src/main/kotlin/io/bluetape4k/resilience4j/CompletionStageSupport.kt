package io.bluetape4k.resilience4j

import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionException
import java.util.concurrent.CompletionStage
import java.util.concurrent.ExecutionException
import kotlin.reflect.KClass

/**
 * Returns a CompletionStage that is recovered from a specific exception.
 *
 * @param completionStage the completionStage which should be recovered from a certain exception
 * @param exceptionTypes the specific exception types that should be recovered
 * @param exceptionHandler the function applied after callable has failed
 * @return a CompletionStage that is recovered from a specific exception.
 */
fun <T> CompletionStage<T>.recover(
    exceptionHandler: (Throwable) -> T,
): CompletionStage<T> {
    return this.exceptionally(exceptionHandler)
}

/**
 * Returns a CompletionStage that is recovered from a specific exception.
 *
 * @param completionStage the completionStage which should be recovered from a certain exception
 * @param exceptionType the specific exception type that should be recovered
 * @param exceptionHandler the function applied after callable has failed
 * @return a CompletionStage that is recovered from a specific exception.
 */
fun <T> CompletionStage<T>.recover(
    exceptionTypes: List<Class<out Throwable>>,
    exceptionHandler: (Throwable) -> T,
): CompletionStage<T> {
    val promise = CompletableFuture<T>()

    this.whenComplete { result, throwable ->
        if (throwable != null) {
            if (throwable is CompletionException || throwable is ExecutionException) {
                tryRecover(exceptionTypes, exceptionHandler, promise, throwable.cause!!)
            } else {
                tryRecover(exceptionTypes, exceptionHandler, promise, throwable)
            }
        } else {
            promise.complete(result)
        }
    }
    return promise
}


/**
 * Returns a CompletionStage that is recovered from a specific exception.
 *
 * @param completionStage the completionStage which should be recovered from a certain exception
 * @param exceptionType the specific exception type that should be recovered
 * @param exceptionHandler the function applied after callable has failed
 * @return a CompletionStage that is recovered from a specific exception.
 */
fun <X: Throwable, T> CompletionStage<T>.recover(
    exceptionType: KClass<X>,
    exceptionHandler: (Throwable?) -> T,
): CompletionStage<T> {
    val promise = CompletableFuture<T>()

    this.whenComplete { result, throwable ->
        if (throwable != null) {
            if (throwable is CompletionException || throwable is ExecutionException) {
                tryRecover(exceptionType, exceptionHandler, promise, throwable.cause!!)
            } else {
                tryRecover(exceptionType, exceptionHandler, promise, throwable)
            }
        } else {
            promise.complete(result)
        }
    }
    return promise
}

private fun <T> tryRecover(
    exceptionTypes: List<Class<out Throwable>>,
    exceptionHandler: (Throwable) -> T,
    promise: CompletableFuture<T>,
    throwable: Throwable,
) {
    if (exceptionTypes.any { it.isAssignableFrom(throwable.javaClass) }) {
        try {
            promise.complete(exceptionHandler.invoke(throwable))
        } catch (fallbackException: Exception) {
            promise.completeExceptionally(fallbackException)
        }
    } else {
        promise.completeExceptionally(throwable)
    }
}

private fun <X: Throwable, T> tryRecover(
    exceptionType: KClass<X>,
    exceptionHandler: (Throwable) -> T,
    promise: CompletableFuture<T>,
    throwable: Throwable,
) {
    if (exceptionType.java.isAssignableFrom(throwable.javaClass)) {
        try {
            promise.complete(exceptionHandler.invoke(throwable))
        } catch (fallbackException: Exception) {
            promise.completeExceptionally(fallbackException)
        }
    } else {
        promise.completeExceptionally(throwable)
    }
}

/**
 * Returns a decorated CompletionStage that is recovered from a specific exception.
 *
 * @param completionStageSupplier a supplier of the completionStage which should be recovered from a certain exception
 * @param exceptionHandler the function applied after callable has failed
 * @return a CompletionStage that is recovered from a specific exception.
 */
fun <T> (() -> CompletionStage<T>).recover(
    exceptionHandler: (Throwable?) -> T,
): () -> CompletionStage<T> = {
    this.invoke().recover(exceptionHandler)
}

/**
 * Returns a decorated CompletionStage that is recovered from a specific exception.
 *
 * @param completionStageSupplier a supplier of the completionStage which should be recovered from a certain exception
 * @param exceptionType the specific exception type that should be recovered
 * @param exceptionHandler the function applied after callable has failed
 * @return a CompletionStage that is recovered from a specific exception.
 */
fun <X: Throwable, T> (() -> CompletionStage<T>).recover(
    exceptionType: KClass<X>,
    exceptionHandler: (Throwable?) -> T,
): () -> CompletionStage<T> = {
    this.invoke().recover(exceptionType, exceptionHandler)
}

/**
 * Returns a decorated CompletionStage that is recovered from a specific exception.
 *
 * @param completionStageSupplier a supplier of the completionStage which should be recovered from a certain exception
 * @param exceptionTypes the specific exception types that should be recovered
 * @param exceptionHandler the function applied after callable has failed
 * @return a CompletionStage that is recovered from a specific exception.
 */
fun <T> (() -> CompletionStage<T>).recover(
    exceptionTypes: List<Class<out Throwable>>,
    exceptionHandler: (Throwable?) -> T,
): () -> CompletionStage<T> = {
    this.invoke().recover(exceptionTypes, exceptionHandler)
}

/**
 * Returns a composed CompletionStage that first executes the CompletionStage and optionally recovers from a specific result.
 *
 * @param <T>              return type of after
 * @param completionStageSupplier the CompletionStage supplier
 * @param resultPredicate the result predicate
 * @param resultHandler the result handler
 * @return a function composed of supplier and exceptionHandler
 */
fun <T> CompletionStage<T>.recover(
    resultPredicate: (T) -> Boolean,
    resultHandler: (T) -> T,
): CompletionStage<T> {
    return this.thenApply { result ->
        if (resultPredicate(result)) {
            resultHandler(result)
        } else {
            result
        }
    }
}

/**
 * Returns a composed CompletionStage that first applies the CompletionStage and then applies {@linkplain
 * BiFunction} {@code after} to the result.
 *
 * @param <T>     return type of after
 * @param completionStageSupplier the CompletionStage supplier
 * @param handler the function applied after supplier
 * @return a function composed of supplier and handler
 */
fun <T, R> (() -> CompletionStage<T>).andThen(
    handler: (T, Throwable?) -> R,
): () -> CompletionStage<R> = {
    this.invoke().handle(handler)
}
