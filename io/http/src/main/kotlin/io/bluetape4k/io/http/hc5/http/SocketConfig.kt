package io.bluetape4k.io.http.hc5.http

import org.apache.hc.core5.http.io.SocketConfig

inline fun socketConfig(initializer: SocketConfig.Builder.() -> Unit): SocketConfig {
    return SocketConfig.custom().apply(initializer).build()
}
