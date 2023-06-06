package io.bluetape4k.io.http.hc5.examples

import io.bluetape4k.io.http.hc5.AbstractHc5Test
import io.bluetape4k.io.http.hc5.io.managedHttpConnectionFactory
import io.bluetape4k.logging.debug
import org.apache.hc.client5.http.ContextBuilder
import org.apache.hc.client5.http.HttpRoute
import org.apache.hc.client5.http.SystemDefaultDnsResolver
import org.apache.hc.client5.http.auth.StandardAuthScheme
import org.apache.hc.client5.http.classic.methods.HttpGet
import org.apache.hc.client5.http.config.ConnectionConfig
import org.apache.hc.client5.http.config.RequestConfig
import org.apache.hc.client5.http.config.TlsConfig
import org.apache.hc.client5.http.cookie.BasicCookieStore
import org.apache.hc.client5.http.cookie.StandardCookieSpec
import org.apache.hc.client5.http.impl.auth.CredentialsProviderBuilder
import org.apache.hc.client5.http.impl.classic.HttpClients
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager
import org.apache.hc.client5.http.socket.ConnectionSocketFactory
import org.apache.hc.client5.http.socket.PlainConnectionSocketFactory
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory
import org.apache.hc.core5.http.ClassicHttpResponse
import org.apache.hc.core5.http.Header
import org.apache.hc.core5.http.HttpHost
import org.apache.hc.core5.http.ParseException
import org.apache.hc.core5.http.config.CharCodingConfig
import org.apache.hc.core5.http.config.Http1Config
import org.apache.hc.core5.http.config.RegistryBuilder
import org.apache.hc.core5.http.impl.io.DefaultClassicHttpResponseFactory
import org.apache.hc.core5.http.impl.io.DefaultHttpRequestWriterFactory
import org.apache.hc.core5.http.impl.io.DefaultHttpResponseParser
import org.apache.hc.core5.http.impl.io.DefaultHttpResponseParserFactory
import org.apache.hc.core5.http.io.HttpMessageParser
import org.apache.hc.core5.http.io.SocketConfig
import org.apache.hc.core5.http.io.entity.EntityUtils
import org.apache.hc.core5.http.message.BasicHeader
import org.apache.hc.core5.http.message.BasicLineParser
import org.apache.hc.core5.http.message.StatusLine
import org.apache.hc.core5.http.ssl.TLS
import org.apache.hc.core5.pool.PoolConcurrencyPolicy
import org.apache.hc.core5.pool.PoolReusePolicy
import org.apache.hc.core5.ssl.SSLContexts
import org.apache.hc.core5.util.CharArrayBuffer
import org.apache.hc.core5.util.TimeValue
import org.apache.hc.core5.util.Timeout
import org.junit.jupiter.api.Test
import java.net.InetAddress
import java.nio.charset.CodingErrorAction

class ClientConfiguration: AbstractHc5Test() {

    @Test
    fun `using client configuration`() {

        val responseParserFactory = object: DefaultHttpResponseParserFactory() {
            override fun create(http1Config: Http1Config): HttpMessageParser<ClassicHttpResponse> {
                val lineParser = object: BasicLineParser() {
                    override fun parseHeader(buffer: CharArrayBuffer?): Header {
                        return try {
                            super.parseHeader(buffer)
                        } catch (e: ParseException) {
                            BasicHeader(buffer.toString(), null)
                        }
                    }
                }
                return DefaultHttpResponseParser(lineParser, DefaultClassicHttpResponseFactory.INSTANCE, http1Config)
            }
        }

        val requestWriterFactory = DefaultHttpRequestWriterFactory()

        // Create HTTP/1.1 protocol configuration
        val h1Config = Http1Config.custom()
            .setMaxHeaderCount(200)
            .setMaxLineLength(2000)
            .build()

        val charCodingConfig = CharCodingConfig.custom()
            .setMalformedInputAction(CodingErrorAction.IGNORE)
            .setUnmappableInputAction(CodingErrorAction.IGNORE)
            .setCharset(Charsets.UTF_8)
            .build()

        // Use a custom connection factory to customize the process of
        // initialization of outgoing HTTP connections. Beside standard connection
        // configuration parameters HTTP connection factory can define message
        // parser / writer routines to be employed by individual connections.
        val connFactory = managedHttpConnectionFactory {
            http1Config(h1Config)
            charCodingConfig(charCodingConfig)
            requestWriterFactory(requestWriterFactory)
            responseParserFactory(responseParserFactory)
        }

        // Client HTTP connection objects when fully initialized can be bound to
        // an arbitrary network socket. The process of network socket initialization,
        // its connection to a remote address and binding to a local one is controlled
        // by a connection socket factory.

        // SSL context for secure connections can be created either based on
        // system or application specific properties.
        val sslContext = SSLContexts.createSystemDefault()

        // Create a registry of custom connection socket factories for supported
        // protocol schemes.
        val socketFactoryRegistry = RegistryBuilder.create<ConnectionSocketFactory>()
            .register("http", PlainConnectionSocketFactory.INSTANCE)
            .register("https", SSLConnectionSocketFactory(sslContext))
            .build()

        // Use custom DNS resolver to override the system DNS resolution.
        val dnsResolver = object: SystemDefaultDnsResolver() {
            override fun resolve(host: String): Array<InetAddress> {
                return if (host.equals("myhost", ignoreCase = true)) {
                    arrayOf(InetAddress.getByAddress(byteArrayOf(127, 0, 0, 1)))
                } else {
                    super.resolve(host)
                }
            }
        }

        // Create a connection manager with custom configuration.
        val connManager = PoolingHttpClientConnectionManager(
            socketFactoryRegistry,
            PoolConcurrencyPolicy.STRICT,
            PoolReusePolicy.LIFO,
            TimeValue.ofMinutes(5),
            null,
            dnsResolver,
            connFactory
        )
        // Configure the connection manager to use socket configuration either
        // by default or for a specific host.
        connManager.defaultSocketConfig = SocketConfig.custom().setTcpNoDelay(true).build()

        // Validate connection after 10 sec of inactivity
        connManager.setDefaultConnectionConfig(
            ConnectionConfig.custom()
                .setConnectTimeout(Timeout.ofSeconds(30))
                .setSocketTimeout(Timeout.ofSeconds(30))
                .setValidateAfterInactivity(TimeValue.ofSeconds(10))
                .setTimeToLive(TimeValue.ofHours(1))
                .build()
        )

        // Use TLS v1.3 only
        connManager.setDefaultTlsConfig(
            TlsConfig.custom()
                .setHandshakeTimeout(Timeout.ofSeconds(30))
                .setSupportedProtocols(TLS.V_1_3)
                .build()
        )

        // Configure total max or per route limits for persistent connections
        // that can be kept in the pool or leased by the connection manager.
        connManager.maxTotal = 100
        connManager.defaultMaxPerRoute = 10
        connManager.setMaxPerRoute(HttpRoute(HttpHost("somehost", 80)), 20)

        // Use custom cookie store if necessary.
        val cookieStore = BasicCookieStore()
        // Use custom credentials provider if neccessary
        val credentialsProvider = CredentialsProviderBuilder.create().build()
        // Create global request configuration
        val defaultRequestConfig = RequestConfig.custom()
            .setCookieSpec(StandardCookieSpec.STRICT)
            .setExpectContinueEnabled(true)
            .setTargetPreferredAuthSchemes(listOf(StandardAuthScheme.NTLM, StandardAuthScheme.DIGEST))
            .setProxyPreferredAuthSchemes(listOf(StandardAuthScheme.BASIC))
            .build()

        // Create an HttpClient with the given custom dependencies and configuration.

        val httpclient = HttpClients.custom()
            .setConnectionManager(connManager)
            .setDefaultCookieStore(cookieStore)
            .setDefaultCredentialsProvider(credentialsProvider)
            // .setProxy(HttpHost("myproxy", 8080))
            .setDefaultRequestConfig(defaultRequestConfig)
            .build()

        httpclient.use { _ ->
            val httpget = HttpGet("$httpbinBaseUrl/get")

            // Request configuration can be overridden at the request level.
            // They will take precedence over the one set at the client level.
            val requestConfig = RequestConfig.copy(defaultRequestConfig)
                .setConnectionRequestTimeout(Timeout.ofSeconds(5))
                .build()
            httpget.config = requestConfig

            // Execution context can be customized locally.
            // Contextual attributes set the local context level will take
            // precedence over those set at the client level.
            val context = ContextBuilder.create()
                .useCookieStore(cookieStore)
                .useCredentialsProvider(credentialsProvider)
                .build()

            log.debug { "Executing request ${httpget.method} ${httpget.uri}" }

            httpclient.execute(httpget, context) { response ->
                log.debug { "--------------------" }
                log.debug { "$httpget -> ${StatusLine(response)}" }
                EntityUtils.consume(response.entity)
            }

            // Last executed request
            log.debug { "request = ${context.request}" }

            // Execution route
            log.debug { "http route = ${context.httpRoute}" }

            // Auth exchanges
            log.debug { "auth exchanges = ${context.authExchanges}" }

            // Cookie origin
            log.debug { "cookie origin = ${context.cookieOrigin}" }

            // Cookie spec used
            log.debug { "cookie spec = ${context.cookieSpec}" }

            // User security token
            log.debug { "user token = ${context.userToken}" }

            log.debug { "context=$context" }
        }
    }
}