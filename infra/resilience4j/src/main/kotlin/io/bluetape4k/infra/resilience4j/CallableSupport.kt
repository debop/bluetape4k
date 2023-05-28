package io.bluetape4k.infra.resilience4j

import io.github.resilience4j.core.CallableUtils
import java.util.concurrent.Callable

inline fun <T: Any> Callable<T>.recover(
    crossinline errorHandler: (Throwable) -> T,
): Callable<T> {
    return CallableUtils
        .recover(this) { error: Throwable ->
            errorHandler(error)
        }
}

inline fun <T: Any, R: Any> Callable<T>.andThen(
    crossinline resultHandler: (result: T) -> R,
): Callable<R> {
    val callable = { this.call() }
    return CallableUtils
        .andThen(callable) { result: T ->
            resultHandler(result)
        }
}

inline fun <T: Any, R: Any> Callable<T>.andThen(
    crossinline handler: (result: T, error: Throwable?) -> R,
): Callable<R> {
    val callable = { this.call() }
    return CallableUtils
        .andThen(callable) { result: T, error: Throwable? ->
            handler(result, error)
        }
}

inline fun <T: Any, R: Any> Callable<T>.andThen(
    crossinline resultHandler: (T) -> R,
    crossinline exceptionHandler: (Throwable) -> R,
): Callable<R> {
    return CallableUtils
        .andThen(
            { this.call() },
            { result: T -> resultHandler(result) },
            { error: Throwable -> exceptionHandler(error) }
        )
}
