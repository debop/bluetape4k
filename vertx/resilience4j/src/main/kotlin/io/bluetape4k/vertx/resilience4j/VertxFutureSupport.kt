package io.bluetape4k.vertx.resilience4j

import io.vertx.core.Future
import io.vertx.core.Promise

inline fun <T> Future<T>.recover(
    crossinline exceptionHandler: (Throwable?) -> T,
): Future<T> {
    return this.andThen { exceptionHandler(it.cause()) }
}

fun <T> Future<T>.recover(
    exceptionTypes: Iterable<Class<out Throwable?>>,
    exceptionHandler: (Throwable?) -> T,
): Future<T> {
    val promise = Promise.promise<T>()

    onSuccess {
        promise.complete(it)
    }
    onFailure { error ->
        tryRecover(exceptionTypes, exceptionHandler, promise, error.cause ?: error)
    }
    return promise.future()
}


fun <T> Future<T>.recover(
    exceptionType: Class<out Throwable?>,
    exceptionHandler: (Throwable?) -> T,
): Future<T> {
    val promise = Promise.promise<T>()

    onSuccess {
        promise.complete(it)
    }
    onFailure { error ->
        tryRecover(exceptionType, exceptionHandler, promise, error.cause ?: error)
    }
//    this.onComplete { ar ->
//        if (ar.failed()) {
//            val throwable = ar.cause()
//            tryRecover(exceptionType, exceptionHandler, promise, throwable.cause ?: throwable)
//        } else {
//            promise.complete(ar.result())
//        }
//    }
    return promise.future()
}


private fun <T> tryRecover(
    exceptionTypes: Iterable<Class<out Throwable?>>,
    exceptionHandler: (Throwable?) -> T,
    promise: Promise<T>,
    throwable: Throwable,
) {
    if (exceptionTypes.any { it.isAssignableFrom(throwable.javaClass) }) {
        try {
            promise.complete(exceptionHandler(throwable))
        } catch (fallbackException: Exception) {
            promise.fail(fallbackException)
        }
    } else {
        promise.fail(throwable)
    }
}

private fun <T> tryRecover(
    exceptionType: Class<out Throwable?>,
    exceptionHandler: (Throwable?) -> T,
    promise: Promise<T>,
    throwable: Throwable,
) {
    if (exceptionType.isAssignableFrom(throwable.javaClass)) {
        try {
            promise.complete(exceptionHandler(throwable))
        } catch (fallbackException: Exception) {
            promise.fail(fallbackException)
        }
    } else {
        promise.fail(throwable)
    }
}

fun <T> (() -> Future<T>).recover(exceptionHandler: (Throwable?) -> T): () -> Future<T> = {
    this.invoke().recover(exceptionHandler)
}

inline fun <T, R> (() -> Future<T>).recover(
    crossinline handler: (T?, Throwable?) -> R,
): () -> Future<R> = {
    this.invoke().compose(
        { result -> Future.succeededFuture(handler(result, null)) },
        { error -> Future.succeededFuture(handler(null, error)) }
    )
}

fun <T> (() -> Future<T>).recover(
    exceptionType: Class<out Throwable>,
    exceptionHandler: (Throwable?) -> T,
): () -> Future<T> = {
    this.invoke().recover(exceptionType, exceptionHandler)
}

fun <T> (() -> Future<T>).recover(
    exceptionTypes: Iterable<Class<out Throwable>>,
    exceptionHandler: (Throwable?) -> T,
): () -> Future<T> = {
    this.invoke().recover(exceptionTypes, exceptionHandler)
}

inline fun <T> (() -> Future<T>).recover(
    crossinline resultPredicate: (T) -> Boolean,
    crossinline resultHandler: (T) -> T,
): () -> Future<T> = {
    this.invoke().map { result ->
        if (resultPredicate(result)) {
            resultHandler(result)
        } else {
            result
        }
    }
}
