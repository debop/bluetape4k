package io.bluetape4k.io.http.hc5.ssl

import org.apache.hc.client5.http.ssl.ClientTlsStrategyBuilder
import org.apache.hc.core5.http.nio.ssl.TlsStrategy
import org.apache.hc.core5.reactor.ssl.SSLBufferMode
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext

inline fun tlsStrategy(
    initializer: ClientTlsStrategyBuilder.() -> Unit,
): TlsStrategy {
    return ClientTlsStrategyBuilder.create().apply(initializer).build()
}

fun tlsStrategyOf(
    sslContext: SSLContext? = null,
    tlsVersions: Array<String>? = null,
    ciphers: Array<String>? = null,
    sslBufferMode: SSLBufferMode = SSLBufferMode.STATIC,
    hostnameVerifier: HostnameVerifier = defaultHostnameVerifier,
): TlsStrategy = tlsStrategy {
    sslContext?.run { setSslContext(sslContext) }
    tlsVersions?.run { setTlsVersions(*tlsVersions) }
    ciphers?.run { setCiphers(*ciphers) }
    setSslBufferMode(sslBufferMode)
    setHostnameVerifier(hostnameVerifier)
}
