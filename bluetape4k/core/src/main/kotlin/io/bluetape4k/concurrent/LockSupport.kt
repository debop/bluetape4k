package io.bluetape4k.concurrent

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import kotlin.time.Duration

/**
 * [CountDownLatch]를 이용하여 `operation`을 수행하고, 대기합니다.
 *
 * ```
 * val result = withLatch {
 *    // do something
 *    countDown()
 *    42
 * }
 * result shouldBeEqualTo 42
 * ```
 *
 * @receiver Int CountDownLatch의 count
 * @param operation [@kotlin.ExtensionFunctionType] Function1<CountDownLatch, T>
 * @return T
 */
inline fun <T> withLatch(count: Int = 1, crossinline operation: CountDownLatch.() -> T): T {
    val latch = CountDownLatch(count)
    val result = futureOf { latch.operation() }
    latch.await()
    return result.get()
}

inline fun <T> withLatch(count: Int = 1, timeout: Duration, crossinline operation: CountDownLatch.() -> T): T {
    val latch = CountDownLatch(count)
    val result = futureOf { latch.operation() }
    return if (latch.await(timeout.inWholeMilliseconds, TimeUnit.MILLISECONDS)) {
        result.get()
    } else {
        throw TimeoutException("operation is timeout")
    }
}
