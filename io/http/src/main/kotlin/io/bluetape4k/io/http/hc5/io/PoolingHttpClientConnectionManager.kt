package io.bluetape4k.io.http.hc5.io

import org.apache.hc.client5.http.DnsResolver
import org.apache.hc.client5.http.HttpRoute
import org.apache.hc.client5.http.SchemePortResolver
import org.apache.hc.client5.http.config.ConnectionConfig
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder
import org.apache.hc.client5.http.io.ManagedHttpClientConnection
import org.apache.hc.client5.http.socket.ConnectionSocketFactory
import org.apache.hc.client5.http.socket.LayeredConnectionSocketFactory
import org.apache.hc.client5.http.socket.PlainConnectionSocketFactory
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory
import org.apache.hc.core5.function.Resolver
import org.apache.hc.core5.http.URIScheme
import org.apache.hc.core5.http.config.Registry
import org.apache.hc.core5.http.config.RegistryBuilder
import org.apache.hc.core5.http.io.HttpConnectionFactory
import org.apache.hc.core5.http.io.SocketConfig
import org.apache.hc.core5.pool.PoolConcurrencyPolicy
import org.apache.hc.core5.pool.PoolReusePolicy
import org.apache.hc.core5.util.TimeValue

inline fun poolingHttpClientConnectionManager(
    initializer: PoolingHttpClientConnectionManagerBuilder.() -> Unit,
): PoolingHttpClientConnectionManager {
    return PoolingHttpClientConnectionManagerBuilder.create().apply(initializer).build()
}

val defaultSocketFactoryRegistry: Registry<ConnectionSocketFactory> by lazy {
    RegistryBuilder.create<ConnectionSocketFactory>()
        .register(URIScheme.HTTP.id, PlainConnectionSocketFactory.getSocketFactory())
        .register(URIScheme.HTTPS.id, SSLConnectionSocketFactory.getSocketFactory())
        .build()
}

fun poolingHttpClientConnectionManagerOf(
    socketFactoryRegistry: Registry<ConnectionSocketFactory> = defaultSocketFactoryRegistry,
    poolConcurrencyPolicy: PoolConcurrencyPolicy = PoolConcurrencyPolicy.STRICT,
    poolReusePolicy: PoolReusePolicy = PoolReusePolicy.LIFO,
    timeToLive: TimeValue = TimeValue.NEG_ONE_MILLISECOND,
    schemePortResolver: SchemePortResolver? = null,
    dnsResolver: DnsResolver? = null,
    connFactory: HttpConnectionFactory<ManagedHttpClientConnection>? = null,
): PoolingHttpClientConnectionManager {
    return PoolingHttpClientConnectionManager(
        socketFactoryRegistry,
        poolConcurrencyPolicy,
        poolReusePolicy,
        timeToLive,
        schemePortResolver,
        dnsResolver,
        connFactory
    )
}

fun poolingHttpClientConnectionManagerOf(
    sslSocketFactory: LayeredConnectionSocketFactory = SSLConnectionSocketFactory.getSocketFactory(),
    poolConcurrencyPolicy: PoolConcurrencyPolicy = PoolConcurrencyPolicy.STRICT,
    poolReusePolicy: PoolReusePolicy = PoolReusePolicy.LIFO,
    schemePortResolver: SchemePortResolver? = null,
    dnsResolver: DnsResolver? = null,
    connFactory: HttpConnectionFactory<ManagedHttpClientConnection>? = null,
    maxConnTotal: Int? = null,
    maxConnPerRoute: Int? = null,
    connectionConfig: ConnectionConfig = ConnectionConfig.DEFAULT,
    socketConfig: SocketConfig = SocketConfig.DEFAULT,
    socketConfigResolver: Resolver<HttpRoute, SocketConfig>? = null,
): PoolingHttpClientConnectionManager {
    return poolingHttpClientConnectionManager {
        setSSLSocketFactory(sslSocketFactory)
        setPoolConcurrencyPolicy(poolConcurrencyPolicy)
        setConnPoolPolicy(poolReusePolicy)
        setSchemePortResolver(schemePortResolver)
        setDnsResolver(dnsResolver)
        setConnectionFactory(connFactory)

        maxConnTotal?.run { setMaxConnTotal(maxConnTotal) }
        maxConnPerRoute?.run { setMaxConnPerRoute(maxConnPerRoute) }
        socketConfigResolver?.run { setSocketConfigResolver(socketConfigResolver) }

        setDefaultConnectionConfig(connectionConfig)
        setDefaultSocketConfig(socketConfig)
    }
}
