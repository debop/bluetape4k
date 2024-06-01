package io.bluetape4k.http.hc5.ssl

import org.apache.hc.core5.ssl.SSLContextBuilder
import org.apache.hc.core5.ssl.SSLContexts
import javax.net.ssl.SSLContext

inline fun sslContext(
    initializer: SSLContextBuilder.() -> Unit,
): SSLContext {
    return SSLContexts.custom().apply(initializer).build()
}

fun sslContextOf(): SSLContext = SSLContexts.createDefault()

fun sslContextOfSystem(): SSLContext = SSLContexts.createSystemDefault()
