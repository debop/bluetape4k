package io.bluetape4k.io.http.hc5.async

import io.bluetape4k.coroutines.support.awaitSuspending
import org.apache.hc.client5.http.async.HttpAsyncClient
import org.apache.hc.client5.http.async.methods.SimpleHttpRequest
import org.apache.hc.client5.http.async.methods.SimpleHttpResponse
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient
import org.apache.hc.client5.http.protocol.HttpClientContext
import org.apache.hc.core5.http.HttpHost
import org.apache.hc.core5.http.nio.AsyncPushConsumer
import org.apache.hc.core5.http.nio.AsyncRequestProducer
import org.apache.hc.core5.http.nio.AsyncResponseConsumer
import org.apache.hc.core5.http.nio.HandlerFactory
import org.apache.hc.core5.http.protocol.HttpContext
import org.apache.hc.core5.reactor.IOReactorStatus

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
    return execute(
        requestProducer,
        responseConsumer,
        pushHandlerFactory,
        context ?: HttpClientContext.create(),
        null
    ).awaitSuspending()
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
    if (status == IOReactorStatus.INACTIVE) {
        start()
    }
    return execute(request, context, null).awaitSuspending()
}

suspend fun <T: Any> CloseableHttpAsyncClient.executeSuspending(
    target: HttpHost,
    requestProducer: AsyncRequestProducer,
    responseConsumer: AsyncResponseConsumer<T>,
    pushHandlerFactory: HandlerFactory<AsyncPushConsumer>? = null,
    context: HttpContext? = null,
): T {
    return execute(
        target,
        requestProducer,
        responseConsumer,
        pushHandlerFactory,
        context ?: HttpClientContext.create(),
        null
    ).awaitSuspending()
}
