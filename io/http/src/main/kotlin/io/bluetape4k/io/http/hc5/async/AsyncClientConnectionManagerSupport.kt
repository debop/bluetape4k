package io.bluetape4k.io.http.hc5.async

import org.apache.hc.client5.http.impl.nio.PoolingAsyncClientConnectionManagerBuilder
import org.apache.hc.client5.http.nio.AsyncClientConnectionManager

inline fun asyncClientConnectionManager(
    initializer: PoolingAsyncClientConnectionManagerBuilder.() -> Unit,
): AsyncClientConnectionManager {
    return PoolingAsyncClientConnectionManagerBuilder.create().apply(initializer).build()
}
