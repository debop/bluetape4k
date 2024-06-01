package io.bluetape4k.http.hc5.ssl

import io.bluetape4k.support.ifTrue
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext

inline fun sslConnectionSocketFactory(
    inintializer: SSLConnectionSocketFactoryBuilder.() -> Unit,
): SSLConnectionSocketFactory {
    return SSLConnectionSocketFactoryBuilder.create().apply(inintializer).build()
}

fun sslConnectionSocketFactoryOf(
    sslContext: SSLContext? = null,
    tlsVersions: Array<String>? = null,
    ciphers: Array<String>? = null,
    hostnameVerifier: HostnameVerifier = defaultHostnameVerifier,
    systemProperties: Boolean? = null,
): SSLConnectionSocketFactory = sslConnectionSocketFactory {
    sslContext?.run { setSslContext(sslContext) }
    tlsVersions?.run { setTlsVersions(*tlsVersions) }
    ciphers?.run { setCiphers(*ciphers) }
    setHostnameVerifier(hostnameVerifier)
    systemProperties?.ifTrue { useSystemProperties() }
}
