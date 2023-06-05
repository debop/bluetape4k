package io.bluetape4k.io.http.hc5.config

import org.apache.hc.client5.http.config.TlsConfig
import org.apache.hc.core5.http.ssl.TLS
import org.apache.hc.core5.http2.HttpVersionPolicy
import org.apache.hc.core5.util.Timeout

@JvmField
val defaultTlsConfig: TlsConfig = TlsConfig.DEFAULT

inline fun tlsConfig(initializer: TlsConfig.Builder.() -> Unit): TlsConfig {
    return TlsConfig.custom().apply(initializer).build()
}

fun tlsConfigOf(
    supportedProtocols: Collection<TLS> = listOf(TLS.V_1_0, TLS.V_1_1, TLS.V_1_2, TLS.V_1_3),
    handshakeTimeout: Timeout? = null,
    supportedCipherSuites: Array<String>? = null,
    versionPolicy: HttpVersionPolicy? = null,
): TlsConfig = tlsConfig {
    setSupportedProtocols(*supportedProtocols.toTypedArray())

    handshakeTimeout?.run { setHandshakeTimeout(handshakeTimeout) }
    supportedCipherSuites?.run { setSupportedCipherSuites(*supportedCipherSuites) }
    versionPolicy?.run { setVersionPolicy(versionPolicy) }
}
