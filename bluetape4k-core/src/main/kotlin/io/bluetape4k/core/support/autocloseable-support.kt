package io.bluetape4k.core.support

import io.bluetape4k.core.asyncWithTimeout
import java.time.Duration

@JvmField
val EMPTY_CLOSE_ERROR_HANDLER: (error: Throwable) -> Unit = { }

/**
 * [AutoCloseable]을 안전하게 close 를 수행합니다.
 *
 * @param errorHandler close 시 예외 발생 시 수행할 handler, 기본은 아무 일도 하지 않는다
 */
inline fun AutoCloseable.closeSafe(errorHandler: (error: Throwable) -> Unit = { }) {
    try {
        close()
    } catch (ignored: Throwable) {
        errorHandler(ignored)
    }
}

/**
 * close 시에 제한시간[timeoutMillis]을 지정하여 수행합니다.
 *
 * @param timeout close 의 최대 수행 시간
 * @param errorHandler close 에서 예외 발생 시 수행할 함수
 */
inline fun AutoCloseable.closeTimeout(
    timeoutMillis: Long = 180,
    crossinline errorHandler: (error: Throwable) -> Unit = {},
) {
    try {
        asyncWithTimeout(timeoutMillis) { closeSafe(errorHandler) }.get()
    } catch (e: Throwable) {
        errorHandler(e.cause ?: e)
    }
}

/**
 * close 시에 제한시간[timeout]을 지정하여 수행합니다.
 *
 * @param timeout close 의 최대 수행 시간
 * @param errorHandler close 에서 예외 발생 시 수행할 함수
 */
inline fun AutoCloseable.closeTimeout(
    timeout: Duration = Duration.ofSeconds(3),
    crossinline errorHandler: (error: Throwable) -> Unit = {},
) {
    closeTimeout(timeout.toMillis(), errorHandler)
}

inline infix fun <T> AutoCloseable.using(action: (AutoCloseable) -> T): T {
    return try {
        action(this)
    } finally {
        closeSafe()
    }
}
