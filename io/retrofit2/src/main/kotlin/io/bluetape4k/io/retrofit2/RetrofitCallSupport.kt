package io.bluetape4k.io.retrofit2

import io.github.resilience4j.decorators.Decorators
import io.github.resilience4j.retry.Retry
import retrofit2.Call
import retrofit2.HttpException
import retrofit2.Response
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService

inline fun <T> retrofit2.Call<T>.executeAsync(
    crossinline cancelHandler: (Throwable?) -> Unit = {},
): CompletableFuture<retrofit2.Response<T>> {
    val promise = CompletableFuture<retrofit2.Response<T>>()

    val callback = object: retrofit2.Callback<T> {
        override fun onResponse(call: Call<T>, response: Response<T>) {
            when {
                response.isSuccessful -> promise.complete(response)
                else                  -> {
                    val ex = HttpException(response)
                    if (call.isCanceled) {
                        cancelHandler(ex)
                        promise.cancel(true)
                    } else {
                        promise.completeExceptionally(ex)
                    }
                }
            }
        }

        override fun onFailure(call: Call<T>, t: Throwable) {
            if (call.isCanceled) {
                cancelHandler(t)
                promise.cancel(true)
            } else {
                promise.completeExceptionally(t)
            }
        }
    }

    enqueue(callback)
    return promise
}

/**
 * Resilience4j [Retry]를 이용하여 Call 비동기 실행을 재시도 할 수 있게 합니다.
 *
 * @param T
 * @param retry Resilience4j [Retry] 인스턴스
 * @param scheduler [Retry] 재시도 스케줄러
 * @param cancelHandler 취소 핸들러
 * @receiver
 * @return 비동기 호출 결과
 */
inline fun <T> retrofit2.Call<T>.executeAsync(
    retry: Retry,
    scheduler: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor(),
    crossinline cancelHandler: (Throwable?) -> Unit = {},
): CompletableFuture<retrofit2.Response<T>> {

    return Decorators
        .ofCompletionStage { executeAsync(cancelHandler) }
        .withRetry(retry, scheduler)
        .get()
        .toCompletableFuture()
}
