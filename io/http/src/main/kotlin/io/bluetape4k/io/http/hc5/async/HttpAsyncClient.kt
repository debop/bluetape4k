package io.bluetape4k.io.http.hc5.async

import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient
import org.apache.hc.client5.http.impl.async.H2AsyncClientBuilder
import org.apache.hc.client5.http.impl.async.HttpAsyncClientBuilder
import org.apache.hc.client5.http.impl.async.HttpAsyncClients
import org.apache.hc.client5.http.nio.AsyncClientConnectionManager
import org.apache.hc.core5.http2.config.H2Config

@JvmField
val defaultHttpAsyncClient: CloseableHttpAsyncClient = HttpAsyncClients.createDefault()

inline fun httpAsyncClient(initializer: HttpAsyncClientBuilder.() -> Unit): CloseableHttpAsyncClient {
    return HttpAsyncClients.custom().apply(initializer).build()
}

fun httpAsyncClientOf(
    cm: AsyncClientConnectionManager = defaultAsyncClientConnectionManager,
): CloseableHttpAsyncClient {
    return httpAsyncClient {
        setConnectionManager(cm)
    }
}

fun httpAsyncClientSystemOf(): CloseableHttpAsyncClient = HttpAsyncClients.createSystem()


@JvmField
val defaultH2AsyncClient: CloseableHttpAsyncClient = HttpAsyncClients.createHttp2Default()

inline fun h2AsyncClient(initializer: H2AsyncClientBuilder.() -> Unit): CloseableHttpAsyncClient {
    return HttpAsyncClients.customHttp2().apply(initializer).build()
}

fun h2AsyncClientOf(): CloseableHttpAsyncClient = HttpAsyncClients.createHttp2Default()

fun h2AsyncClientOf(
    h2config: H2Config = H2Config.DEFAULT,
): CloseableHttpAsyncClient {
    return h2AsyncClient {
        setH2Config(h2config)
    }
}

fun h2AsyncClientSystemOf(): CloseableHttpAsyncClient = HttpAsyncClients.createHttp2System()
