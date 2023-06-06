package io.bluetape4k.io.http.hc5.http

import org.apache.hc.client5.http.config.RequestConfig

inline fun requestConfig(
    initializer: RequestConfig.Builder.() -> Unit,
): RequestConfig {
    return RequestConfig.custom().apply(initializer).build()
}

fun requestConfigOf(): RequestConfig = requestConfig {}
