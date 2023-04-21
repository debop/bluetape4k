package io.bluetape4k.io.retrofit2.clients.ahc

import io.bluetape4k.io.http.ahc.defaultAsyncHttpClient
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import org.asynchttpclient.AsyncCompletionHandler
import org.asynchttpclient.AsyncHttpClient
import org.asynchttpclient.BoundRequestBuilder
import org.asynchttpclient.Response
import org.asynchttpclient.extras.retrofit.AsyncHttpClientCallFactory
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException


/**
 * Retrofit2 에서 OkHttp3를 대신 AsyncHttpClient를 사용할 수 있도록 해주는 Call.Factroy 입니다.
 *
 * ```
 * val factory = asyncHttpClientCallFactory {
 *      httpClient(defaultAsyncHttpClient)
 * }
 * ```
 * @param httpClientSupplier [AsyncHttpClient] 제공 함수
 * @return okhttp3.Call.Factory
 */
inline fun asyncHttpClientCallFactory(
    initializer: AsyncHttpClientCallFactory.AsyncHttpClientCallFactoryBuilder.() -> Unit,
): okhttp3.Call.Factory {
    return AsyncHttpClientCallFactory.builder().apply(initializer).build()
}

/**
 * Retrofit2에서 OkHttp3를 대신 AsyncHttpClient를 사용할 수 있도록 해주는 Call.Factroy 입니다.
 * @param client [AsyncHttpClient] 제공 함수
 * @return okhttp3.Call.Factory
 */
fun asyncHttpClientCallFactoryOf(
    client: AsyncHttpClient = defaultAsyncHttpClient,
): okhttp3.Call.Factory {
    return asyncHttpClientCallFactory {
        httpClient(client)
    }
}

/**
 * [BoundRequestBuilder] 를 Coroutines 를 이용하여 실행합니다.
 *
 * @receiver BoundRequestBuilder
 * @return Response
 */
suspend fun BoundRequestBuilder.coExecute(): Response = suspendCancellableCoroutine { cont ->
    execute(DefaultCoroutineCompletionHandler(cont))
}

internal class DefaultCoroutineCompletionHandler(
    private val cont: CancellableContinuation<Response>,
): AsyncCompletionHandler<Response>() {

    override fun onCompleted(response: Response): Response {
        return response.apply { cont.resume(this) }
    }

    override fun onThrowable(t: Throwable) {
        cont.resumeWithException(t)
    }
}
