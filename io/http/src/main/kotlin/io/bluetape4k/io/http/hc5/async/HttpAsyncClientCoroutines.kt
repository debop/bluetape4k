package io.bluetape4k.io.http.hc5.async

import kotlinx.coroutines.suspendCancellableCoroutine
import org.apache.hc.client5.http.async.HttpAsyncClient
import org.apache.hc.client5.http.async.methods.SimpleHttpRequest
import org.apache.hc.client5.http.async.methods.SimpleHttpResponse
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient
import org.apache.hc.client5.http.protocol.HttpClientContext
import org.apache.hc.core5.concurrent.FutureCallback
import org.apache.hc.core5.http.HttpHost
import org.apache.hc.core5.http.nio.AsyncPushConsumer
import org.apache.hc.core5.http.nio.AsyncRequestProducer
import org.apache.hc.core5.http.nio.AsyncResponseConsumer
import org.apache.hc.core5.http.nio.HandlerFactory
import org.apache.hc.core5.http.protocol.HttpContext
import org.apache.hc.core5.reactor.IOReactorStatus
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Coroutines 환경에서 [HttpAsyncClient.execute]를 실행합니다.
 *
 * @param T 반환 수형
 * @param requestProducer  비동기 Request 를 생성하는 Producer
 * @param responseConsumer 응답을 처리하는 Consumer
 * @param pushHandlerFactory 비동기 Push consumer 를 생성하는 Factory
 * @param context  [HttpContext] 인스턴스
 * @return [HttpResponse] 를 `T` 로 변환한 값
 */
suspend fun <T: Any> HttpAsyncClient.executeSuspending(
    requestProducer: AsyncRequestProducer,
    responseConsumer: AsyncResponseConsumer<T>,
    pushHandlerFactory: HandlerFactory<AsyncPushConsumer>? = null,
    context: HttpContext? = null,
): T {
    return suspendCancellableCoroutine { cont ->
        val callback = object: FutureCallback<T> {
            override fun completed(result: T) {
                cont.resume(result)
            }

            override fun failed(ex: Exception) {
                cont.resumeWithException(ex)
            }

            override fun cancelled() {
                cont.cancel(null)
            }
        }
        val future = execute(
            requestProducer,
            responseConsumer,
            pushHandlerFactory,
            context ?: HttpClientContext.create(),
            callback
        )
        cont.invokeOnCancellation { future.cancel(true) }
    }
}

/**
 * Coroutines 환경에서 [CloseableHttpAsyncClient.execute]를 실행합니다.
 *
 * @param request 요청 자료
 * @param context [HttpClientContext] 인스턴스
 * @return [SimpleHttpResponse] 인스턴스
 */
suspend fun CloseableHttpAsyncClient.executeSuspending(
    request: SimpleHttpRequest,
    context: HttpClientContext = HttpClientContext.create(),
): SimpleHttpResponse {
    return suspendCancellableCoroutine { cont ->
        if (status == IOReactorStatus.INACTIVE) {
            start()
        }

        val callback = object: FutureCallback<SimpleHttpResponse> {
            override fun completed(result: SimpleHttpResponse) {
                cont.resume(result)
            }

            override fun failed(ex: Exception) {
                cont.resumeWithException(ex)
            }

            override fun cancelled() {
                cont.cancel(null)
            }
        }
        val future = execute(request, context, callback)
        cont.invokeOnCancellation { future.cancel(true) }
    }
}

suspend fun <T: Any> CloseableHttpAsyncClient.executeSuspending(
    target: HttpHost,
    requestProducer: AsyncRequestProducer,
    responseConsumer: AsyncResponseConsumer<T>,
    pushHandlerFactory: HandlerFactory<AsyncPushConsumer>? = null,
    context: HttpContext? = null,
): T {
    return suspendCancellableCoroutine { cont ->
        if (status == IOReactorStatus.INACTIVE) {
            start()
        }

        val callback = object: FutureCallback<T> {
            override fun completed(result: T) {
                cont.resume(result)
            }

            override fun failed(ex: Exception) {
                cont.resumeWithException(ex)
            }

            override fun cancelled() {
                cont.cancel(null)
            }
        }

        val future = execute(
            target,
            requestProducer,
            responseConsumer,
            pushHandlerFactory,
            context ?: HttpClientContext.create(),
            callback
        )
        cont.invokeOnCancellation { future.cancel(true) }
    }
}
