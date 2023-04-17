package io.bluetape4k.utils.resilience4j

import io.github.resilience4j.core.SupplierUtils
import java.util.function.Supplier

inline fun <T: Any> Supplier<T>.recover(crossinline errorHandler: (Throwable) -> T): Supplier<T> =
    SupplierUtils.recover({ this.get() },
        { errorHandler(it) })

inline fun <T: Any, R: Any> Supplier<T>.andThen(crossinline resultHandler: (T) -> R): Supplier<R> =
    SupplierUtils.andThen({ this.get() },
        { result: T -> resultHandler(result) })

inline fun <T: Any, R: Any> Supplier<T>.andThen(crossinline handler: (T, Throwable?) -> R): Supplier<R> =
    SupplierUtils.andThen({ this.get() },
        { result: T, error: Throwable? -> handler(result, error) })

inline fun <T: Any, R: Any> Supplier<T>.andThen(
    crossinline resultHandler: (T) -> R,
    crossinline errorHandler: (Throwable) -> R,
): Supplier<R> =
    SupplierUtils.andThen({ this.get() },
        { result: T -> resultHandler(result) },
        { error: Throwable -> errorHandler(error) })
