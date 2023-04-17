package io.bluetape4k.utils.resilience4j

import io.github.resilience4j.core.CallableUtils
import java.util.concurrent.Callable

inline fun <T: Any> Callable<T>.recover(crossinline errorHandler: (error: Throwable) -> T): Callable<T> =
    CallableUtils.recover({ this.call() },
        { error: Throwable -> errorHandler(error) })

inline fun <T: Any, R: Any> Callable<T>.andThen(crossinline resultHandler: (result: T) -> R): Callable<R> =
    CallableUtils.andThen({ this.call() },
        { result: T -> resultHandler(result) })

inline fun <T: Any, R: Any> Callable<T>.andThen(crossinline handler: (result: T, error: Throwable?) -> R): Callable<R> =
    CallableUtils.andThen({ this.call() },
        { result: T, error: Throwable? -> handler(result, error) })

inline fun <T: Any, R: Any> Callable<T>.andThen(
    crossinline resultHandler: (T) -> R,
    crossinline errorHandler: (error: Throwable) -> R,
): Callable<R> =
    CallableUtils.andThen({ this.call() },
        { result: T -> resultHandler(result) },
        { error: Throwable -> errorHandler(error) })
