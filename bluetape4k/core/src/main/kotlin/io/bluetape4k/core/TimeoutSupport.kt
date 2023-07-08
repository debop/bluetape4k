package io.bluetape4k.core

import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.time.Duration

/**
 * 제한시간을 두고 [action]을 비동기로 실행합니다. 제한시간이 지나면 Exception을 가지는 [CompletableFuture]를 반환합니다.
 *
 * 참고: [Asynchronous timeouts with CompletableFutures in Java 8 and Java 9](http://iteratrlearning.com/java9/2016/09/13/java9-timeouts-completablefutures.html)
 *
 * @param timeoutMillis 제한 시간
 * @param action 비동기로 실행할 코드 블럭
 * @return [action]의 실행 결과를 담은 [CompletableFuture], 제한시간이 초과되면 [TimeoutException]을 담은 [CompletableFuture]를 반환합니다.
 */
inline fun <T> asyncRunWithTimeout(timeoutMillis: Long, crossinline action: () -> T): CompletableFuture<T> {
    val executor = Executors.newSingleThreadExecutor()
    return CompletableFuture
        .supplyAsync({ action() }, executor)
        .orTimeout(timeoutMillis.coerceAtLeast(10L), TimeUnit.MILLISECONDS)
        .whenCompleteAsync { _, _ ->
            // action을 즉시 강제 종료시킨다
            executor.shutdown()
        }
}

/**
 * 제한시간을 두고 [action]을 비동기로 실행합니다. 제한시간이 지나면 Exception을 가지는 [CompletableFuture]를 반환합니다.
 *
 * 참고: [Asynchronous timeouts with CompletableFutures in Java 8 and Java 9](http://iteratrlearning.com/java9/2016/09/13/java9-timeouts-completablefutures.html)
 *
 * @param timeout 제한 시간
 * @param action 비동기로 실행할 코드 블럭
 * @return [action]의 실행 결과를 담은 [CompletableFuture], 제한시간이 초과되면 [TimeoutException]을 담은 [CompletableFuture]를 반환합니다.
 */
inline fun <T> asyncRunWithTimeout(timeout: Duration, crossinline action: () -> T): CompletableFuture<T> {
    return asyncRunWithTimeout(timeout.inWholeMilliseconds, action)
}

/**
 * Timeout 내에서 [block]을 실행합니다. [block]이 [timeoutMillis] 시간 내에 종료되지 않으면 null 을 반환합니다.
 *
 * @param timeoutMillis 실행 제한 시간 (millisecond)
 * @param block 실행할 block
 * @return [block]의 실행 결과, [timeoutMillis] 시간 내에 종료되지 않으면 null
 */
inline fun <T: Any> withTimeoutOrNull(timeoutMillis: Long, crossinline action: () -> T): T? {
    return try {
        asyncRunWithTimeout(timeoutMillis, action).get()
    } catch (e: Exception) {
        null
    }
}

/**
 * Timeout 내에서 [block]을 실행합니다. [block]이 [timeoutMillis] 시간 내에 종료되지 않으면 null 을 반환합니다.
 *
 * @param timeout 제한 시간
 * @param block 실행할 block
 * @return [block]의 실행 결과, [timeout] 시간 내에 종료되지 않으면 null
 */
inline fun <T: Any> withTimeoutOrNull(timeout: Duration, crossinline action: () -> T): T? {
    return withTimeoutOrNull(timeout.inWholeMilliseconds, action)
}
