package io.bluetape4k.http.hc5.async

import org.apache.hc.client5.http.impl.nio.PoolingAsyncClientConnectionManager
import org.apache.hc.client5.http.impl.nio.PoolingAsyncClientConnectionManagerBuilder

@JvmField
val defaultAsyncClientConnectionManager: PoolingAsyncClientConnectionManager =
    PoolingAsyncClientConnectionManagerBuilder.create().build()

inline fun asyncClientConnectionManager(
    initializer: PoolingAsyncClientConnectionManagerBuilder.() -> Unit,
): PoolingAsyncClientConnectionManager {
    return PoolingAsyncClientConnectionManagerBuilder.create().apply(initializer).build()
}
