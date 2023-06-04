package io.bluetape4k.io.http.hc5.async

import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient
import org.apache.hc.client5.http.impl.async.H2AsyncClientBuilder
import org.apache.hc.client5.http.impl.async.HttpAsyncClientBuilder
import org.apache.hc.client5.http.impl.async.HttpAsyncClients
import org.apache.hc.client5.http.impl.async.MinimalH2AsyncClient
import org.apache.hc.client5.http.impl.async.MinimalHttpAsyncClient
import org.apache.hc.client5.http.nio.AsyncClientConnectionManager
import org.apache.hc.client5.http.ssl.DefaultClientTlsStrategy
import org.apache.hc.core5.http.config.Http1Config
import org.apache.hc.core5.http.nio.ssl.TlsStrategy
import org.apache.hc.core5.http2.config.H2Config
import org.apache.hc.core5.reactor.IOReactorConfig

inline fun httpAsyncClient(initializer: HttpAsyncClientBuilder.() -> Unit): CloseableHttpAsyncClient {
    return HttpAsyncClients.custom().apply(initializer).build()
}

fun httpAsyncClientOf(): CloseableHttpAsyncClient = HttpAsyncClients.createDefault()

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

fun minimalHttpAsyncClientOf(
    h2config: H2Config = H2Config.DEFAULT,
    h1config: Http1Config = Http1Config.DEFAULT,
    ioReactorConfig: IOReactorConfig = IOReactorConfig.DEFAULT,
    connMgr: AsyncClientConnectionManager = asyncClientConnectionManager { },
): MinimalHttpAsyncClient {
    return HttpAsyncClients.createMinimal(h2config, h1config, ioReactorConfig, connMgr)
}

fun minimalH2AsyncClientOf(
    h2config: H2Config,
    ioReactorConfig: IOReactorConfig = IOReactorConfig.DEFAULT,
    tlsStrategy: TlsStrategy = DefaultClientTlsStrategy.getDefault(),
): MinimalH2AsyncClient {
    return HttpAsyncClients.createHttp2Minimal(h2config, ioReactorConfig, tlsStrategy)
}
