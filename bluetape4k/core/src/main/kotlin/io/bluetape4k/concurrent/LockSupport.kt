package io.bluetape4k.concurrent

import java.time.Duration
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

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
    return if (latch.await(timeout.toMillis(), TimeUnit.MILLISECONDS)) {
        result.get()
    } else {
        throw TimeoutException("operation is timeout")
    }
}

/**
 * [ReentrantReadWriteLock]의 read lock을 걸고 `block`을 실행한다
 *
 * ```
 * val rwLock = ReenterantReadWriteLock()
 * rwLock.withReadLock {
 *     // do work
 * }
 * ```
 * @see [ReentrantReadWriteLock.read]
 *
 * @param block Function0<T>
 * @return T
 */
@Deprecated("use read", replaceWith = ReplaceWith("this.read(block)"))
inline fun <T> ReentrantReadWriteLock.withReadLock(block: () -> T): T {
    return read { block() }
}

/**
 * [ReentrantReadWriteLock]의 write lock을 걸고 `block`을 실행한다.
 *
 * ```
 * val rwLock = ReenterantReadWriteLock()
 * rwLock.withWriteLock {
 *     // do work
 * }
 * ```
 * @see [ReentrantReadWriteLock.write]
 *
 * @param block Function0<T>
 * @return T
 */
@Deprecated("use write", replaceWith = ReplaceWith("this.write(block)"))
inline fun <T> ReentrantReadWriteLock.withWriteLock(block: () -> T): T {
    return write { block() }
}
