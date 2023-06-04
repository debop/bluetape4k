package io.bluetape4k.io.http.hc5.io

import org.apache.hc.client5.http.impl.io.ManagedHttpClientConnectionFactory
import org.apache.hc.client5.http.io.ManagedHttpClientConnection
import org.apache.hc.core5.http.io.HttpConnectionFactory

inline fun managedHttpConnectionFactory(
    initializer: ManagedHttpClientConnectionFactory.Builder.() -> Unit,
): HttpConnectionFactory<ManagedHttpClientConnection> {
    return ManagedHttpClientConnectionFactory.builder().apply(initializer).build()
}
