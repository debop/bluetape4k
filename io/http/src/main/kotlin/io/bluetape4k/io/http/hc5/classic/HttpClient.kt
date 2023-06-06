package io.bluetape4k.io.http.hc5.classic

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder
import org.apache.hc.client5.http.impl.classic.HttpClients
import org.apache.hc.client5.http.impl.classic.MinimalHttpClient
import org.apache.hc.client5.http.io.HttpClientConnectionManager


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

fun httpClientOf(
    connectionManager: HttpClientConnectionManager,
): CloseableHttpClient = httpClient {
    setConnectionManager(connectionManager)
}

fun systemHttpClientOf(): CloseableHttpClient = HttpClients.createSystem()

fun minimalHttpClientOf(
    connManager: HttpClientConnectionManager = defaultHttpClientConnectionManager,
): MinimalHttpClient {
    return HttpClients.createMinimal(connManager)
}
