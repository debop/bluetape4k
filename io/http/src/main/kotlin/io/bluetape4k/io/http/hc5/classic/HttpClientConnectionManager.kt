package io.bluetape4k.io.http.hc5.classic

import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder
import org.apache.hc.client5.http.io.HttpClientConnectionManager

@JvmField
val defaultHttpClientConnectionManager: PoolingHttpClientConnectionManager =
    PoolingHttpClientConnectionManagerBuilder.create().build()

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
): PoolingHttpClientConnectionManager {
    return PoolingHttpClientConnectionManagerBuilder.create().apply(initializer).build()
}
