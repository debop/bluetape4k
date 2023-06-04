package io.bluetape4k.io.http.hc5.classic

import org.apache.hc.client5.http.config.RequestConfig
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient
import org.apache.hc.client5.http.impl.async.HttpAsyncClientBuilder
import org.apache.hc.client5.http.impl.async.HttpAsyncClients
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder
import org.apache.hc.client5.http.impl.classic.HttpClients
import org.apache.hc.client5.http.impl.io.ManagedHttpClientConnectionFactory
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder
import org.apache.hc.client5.http.io.HttpClientConnectionManager
import org.apache.hc.client5.http.io.ManagedHttpClientConnection
import org.apache.hc.core5.http.io.HttpConnectionFactory

/**
 * Apache HttpComponent 5 의 [HttpClientConnectionManager]를 빌드합니다.
 *
 * ```
 * val cm = httpClientConnectionManager {
 *      setMaxConnPerRoute(5)
 *      setMaxConnTotal(5)
 * }
 * val httpClient = httpClient { setConnectionManager(cm) }
 * ```
 *
 * @param initializer
 * @receiver
 * @return [HttpClientConnectionManager] instance
 */
inline fun httpClientConnectionManager(
    initializer: PoolingHttpClientConnectionManagerBuilder.() -> Unit,
): HttpClientConnectionManager {
    return PoolingHttpClientConnectionManagerBuilder.create().apply(initializer).build()
}

inline fun httpConnectionFactory(
    initializer: ManagedHttpClientConnectionFactory.Builder.() -> Unit,
): HttpConnectionFactory<ManagedHttpClientConnection> {
    return ManagedHttpClientConnectionFactory.builder().apply(initializer).build()
}

/**
 * Apache HttpComponent 5 의 [CloseableHttpClient] 를 빌드합니다.
 *
 * ```
 * val cm = httpClientConnectionManager {
 *      setMaxConnPerRoute(5)
 *      setMaxConnTotal(5)
 * }
 * val httpClient = httpClient { setConnectionManager(cm) }
 * ```
 *
 * @param initializer
 * @receiver
 * @return
 */
inline fun httpClient(
    initializer: HttpClientBuilder.() -> Unit,
): CloseableHttpClient {
    return HttpClientBuilder.create().apply(initializer).build()
}

fun defaultHttpClient(): CloseableHttpClient = HttpClients.createDefault()

fun httpClientOf(): CloseableHttpClient = HttpClients.createDefault()

inline fun httpAsyncClient(
    initializer: HttpAsyncClientBuilder.() -> Unit,
): CloseableHttpAsyncClient {
    return HttpAsyncClients.custom().apply(initializer).build()
}

fun httpAsyncClientOf(): CloseableHttpAsyncClient = HttpAsyncClients.createDefault()

inline fun requestConfig(
    initializer: RequestConfig.Builder.() -> Unit,
): RequestConfig {
    return RequestConfig.custom().apply(initializer).build()
}

fun requestConfigOf(): RequestConfig = requestConfig {}
