package io.bluetape4k.http.hc5.async

import io.bluetape4k.coroutines.support.coAwait
import org.apache.hc.client5.http.DnsResolver
import org.apache.hc.client5.http.SystemDefaultDnsResolver
import org.apache.hc.client5.http.impl.async.HttpAsyncClients
import org.apache.hc.client5.http.impl.async.MinimalH2AsyncClient
import org.apache.hc.client5.http.impl.async.MinimalHttpAsyncClient
import org.apache.hc.client5.http.nio.AsyncClientConnectionManager
import org.apache.hc.client5.http.protocol.HttpClientContext
import org.apache.hc.client5.http.ssl.DefaultClientTlsStrategy
import org.apache.hc.core5.concurrent.FutureCallback
import org.apache.hc.core5.http.HttpHost
import org.apache.hc.core5.http.config.Http1Config
import org.apache.hc.core5.http.nio.AsyncClientEndpoint
import org.apache.hc.core5.http.nio.ssl.TlsStrategy
import org.apache.hc.core5.http.protocol.HttpContext
import org.apache.hc.core5.http2.config.H2Config
import org.apache.hc.core5.reactor.IOReactorConfig


@JvmField
val defaultMinimalHttpAsyncClient: MinimalHttpAsyncClient = HttpAsyncClients.createMinimal()

@JvmField
val defaultMinimalH2AsyncClient: MinimalH2AsyncClient = HttpAsyncClients.createHttp2Minimal()

/**
 * Creates {@link MinimalHttpAsyncClient} instance optimized for
 * HTTP/1.1 and HTTP/2 message transport without advanced HTTP protocol
 * functionality.
 *
 * @param h2config
 * @param h1config
 * @param ioReactorConfig
 * @param connMgr
 * @return
 */
fun minimalHttpAsyncClientOf(
    h2config: H2Config = H2Config.DEFAULT,
    h1config: Http1Config = Http1Config.DEFAULT,
    ioReactorConfig: IOReactorConfig = IOReactorConfig.DEFAULT,
    connMgr: AsyncClientConnectionManager = defaultAsyncClientConnectionManager,
): MinimalHttpAsyncClient {
    return HttpAsyncClients.createMinimal(h2config, h1config, ioReactorConfig, connMgr)
}

/**
 * Creates [MinimalH2AsyncClient] instance optimized for HTTP/2 multiplexing message
 * transport without advanced HTTP protocol functionality.
 *
 * @param h2config
 * @param ioReactorConfig
 * @param tlsStrategy
 * @return
 */
fun minimalH2AsyncClientOf(
    h2config: H2Config,
    ioReactorConfig: IOReactorConfig = IOReactorConfig.DEFAULT,
    dnsResolver: DnsResolver = SystemDefaultDnsResolver.INSTANCE,
    tlsStrategy: TlsStrategy = DefaultClientTlsStrategy.getDefault(),
): MinimalH2AsyncClient {
    return HttpAsyncClients.createHttp2Minimal(h2config, ioReactorConfig, dnsResolver, tlsStrategy)
}

suspend fun MinimalHttpAsyncClient.leaseSuspending(
    host: HttpHost,
    context: HttpContext = HttpClientContext.create(),
    callback: FutureCallback<AsyncClientEndpoint>? = null,
): AsyncClientEndpoint {
    return lease(host, context, callback).coAwait()
}
