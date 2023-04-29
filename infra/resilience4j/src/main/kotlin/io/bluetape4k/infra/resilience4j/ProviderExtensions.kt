package io.bluetape4k.infra.resilience4j

import io.github.resilience4j.core.SupplierUtils

/**
 * 인자가 없으면서 <T> 수형을 반환하는 함수 형태
 */
typealias Provider<T> = () -> T


/**
 * `Provider` 실행 시 예외가 발생하면 데체 값을 제공하도록 합니다.
 *
 * @receiver Provider<T>
 * @param errorHandler Function1<[@kotlin.ParameterName] Exception, T>
 * @return Provider<T>
 */
inline fun <T: Any> Provider<T>.recover(
    crossinline errorHandler: (Throwable) -> T,
): Provider<T> = {
    SupplierUtils
        .recover(this) { error: Throwable -> errorHandler(error) }
        .get()
}

inline fun <T: Any, R: Any> Provider<T>.andThen(
    crossinline resultHandler: (T) -> R,
): Provider<R> = {
    SupplierUtils
        .andThen({ this.invoke() }, { result: T -> resultHandler(result) })
        .get()
}

inline fun <T: Any, R: Any> Provider<T>.andThen(
    crossinline handler: (T, Throwable?) -> R,
): Provider<R> = {
    SupplierUtils
        .andThen({ this.invoke() }, { result: T, error: Throwable? -> handler(result, error) })
        .get()
}

inline fun <T: Any, R: Any> Provider<T>.andThen(
    crossinline resultHandler: (T) -> R,
    crossinline errorHandler: (Throwable) -> R,
): Provider<R> = {
    SupplierUtils
        .andThen({ this.invoke() },
            { result: T -> resultHandler(result) },
            { error: Throwable -> errorHandler(error) })
        .get()
}
