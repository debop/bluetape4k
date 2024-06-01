package io.bluetape4k.resilience4j

import io.github.resilience4j.core.CallableUtils
import java.util.concurrent.Callable
import kotlin.reflect.KClass

/**
 * Returns a composed function that first applies the Callable and then applies the
 * resultHandler.
 *
 * @param <T>           return type of callable
 * @param <R>           return type of handler
 * @param callable the callable
 * @param resultHandler the function applied after callable
 * @return a function composed of supplier and resultHandler
 */
inline fun <T: Any, R: Any> Callable<T>.andThen(
    crossinline resultHandler: (result: T) -> R,
): Callable<R> {
    return CallableUtils.andThen(this) { result: T -> resultHandler(result) }
}

/**
 * Returns a composed function that first applies the Callable and then applies {@linkplain
 * BiFunction} {@code after} to the result.
 *
 * @param <T>     return type of callable
 * @param <R>     return type of handler
 * @param callable the callable
 * @param handler the function applied after callable
 * @return a function composed of supplier and handler
 */
inline fun <T: Any, R: Any> Callable<T>.andThen(
    crossinline handler: (result: T, error: Throwable?) -> R,
): Callable<R> {
    return CallableUtils.andThen(this) { result: T, error: Throwable? ->
        handler(result, error)
    }
}

/**
 * Returns a composed function that first applies the Callable and then applies either the
 * resultHandler or exceptionHandler.
 *
 * @param <T>              return type of callable
 * @param <R>              return type of resultHandler and exceptionHandler
 * @param callable the callable
 * @param resultHandler    the function applied after callable was successful
 * @param exceptionHandler the function applied after callable has failed
 * @return a function composed of supplier and handler
 */
inline fun <T: Any, R: Any> Callable<T>.andThen(
    crossinline resultHandler: (T) -> R,
    crossinline exceptionHandler: (Throwable) -> R,
): Callable<R> {
    return CallableUtils.andThen(this,
        { result: T -> resultHandler(result) },
        { error: Throwable -> exceptionHandler(error) })
}


/**
 * Returns a composed function that first executes the Callable and optionally recovers from an
 * exception.
 *
 * @param <T>              return type of after
 * @param callable the callable which should be recovered from a certain exception
 * @param exceptionHandler the exception handler
 * @return a function composed of callable and exceptionHandler
 */
inline fun <T: Any> Callable<T>.recover(
    crossinline errorHandler: (Throwable) -> T,
): Callable<T> {
    return CallableUtils.recover(this) { error: Throwable ->
        errorHandler(error)
    }
}

/**
 * Returns a composed Callable that first executes the Callable and optionally recovers from a specific result.
 *
 * @param <T>              return type of after
 * @param callable the callable
 * @param resultPredicate the result predicate
 * @param resultHandler the result handler
 * @return a function composed of supplier and exceptionHandler
 */
inline fun <T: Any> Callable<T>.recover(
    crossinline resultPredicate: (T) -> Boolean,
    crossinline resultHandler: (T) -> T,
): Callable<T> {
    return CallableUtils.recover(this, { resultPredicate.invoke(it) }) { result: T ->
        resultHandler(result)
    }
}

/**
 * Returns a composed function that first executes the Callable and optionally recovers from an
 * exception.
 *
 * @param <T>              return type of after
 * @param callable the callable which should be recovered from a certain exception
 * @param exceptionTypes the specific exception types that should be recovered
 * @param exceptionHandler the exception handler
 * @return a function composed of supplier and exceptionHandler
 */
inline fun <T: Any> Callable<T>.recover(
    exceptionTypes: List<Class<out Throwable>>,
    crossinline exceptionHandler: (Throwable) -> T,
): Callable<T> {
    return CallableUtils.recover(this, exceptionTypes) { error: Throwable ->
        exceptionHandler(error)
    }
}

/**
 * Returns a composed function that first executes the Callable and optionally recovers from an
 * exception.
 *
 * @param <T>              return type of after
 * @param callable the callable which should be recovered from a certain exception
 * @param exceptionType the specific exception type that should be recovered
 * @param exceptionHandler the exception handler
 * @return a function composed of callable and exceptionHandler
 */
inline fun <X: Throwable, T: Any> Callable<T>.recover(
    exceptionType: KClass<X>,
    crossinline exceptionHandler: (Throwable) -> T,
): Callable<T> {
    return CallableUtils.recover(this, exceptionType.java) { error: Throwable ->
        exceptionHandler(error)
    }
}
