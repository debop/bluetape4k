package io.bluetape4k.io.http.hc5.examples

import io.bluetape4k.coroutines.support.awaitSuspending
import io.bluetape4k.io.http.hc5.AbstractHc5Test
import io.bluetape4k.io.http.hc5.async.asyncClientConnectionManager
import io.bluetape4k.io.http.hc5.async.httpAsyncClientOf
import io.bluetape4k.io.http.hc5.async.methods.simpleHttpRequestOf
import io.bluetape4k.io.http.hc5.async.methods.simpleRequestProducerOf
import io.bluetape4k.io.http.hc5.config.connectionConfigOf
import io.bluetape4k.io.http.hc5.config.tlsConfigOf
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import kotlinx.coroutines.test.runTest
import org.apache.hc.client5.http.async.methods.SimpleResponseConsumer
import org.apache.hc.client5.http.config.TlsConfig
import org.apache.hc.core5.http.HttpHost
import org.apache.hc.core5.http.Method
import org.apache.hc.core5.http.URIScheme
import org.apache.hc.core5.http.message.StatusLine
import org.apache.hc.core5.http.ssl.TLS
import org.apache.hc.core5.util.TimeValue
import org.apache.hc.core5.util.Timeout
import org.junit.jupiter.api.Test
import kotlin.time.Duration.Companion.seconds

class AsyncClientConnectionConfig: AbstractHc5Test() {

    companion object: KLogging()

    @Test
    fun `connection configuration on a per-route or per-host`() = runTest(timeout = 60.seconds) {
        val cm = asyncClientConnectionManager {
            setConnectionConfigResolver { route ->
                if (route.isSecure) {
                    connectionConfigOf(
                        connectTimeout = Timeout.ofMinutes(2),
                        socketTimeout = Timeout.ofMinutes(2),
                        valiateAfterInactivity = TimeValue.ofMinutes(1),
                        timeToLive = TimeValue.ofHours(1)
                    )
                } else {
                    connectionConfigOf(
                        connectTimeout = Timeout.ofMinutes(1),
                        socketTimeout = Timeout.ofMinutes(1),
                        valiateAfterInactivity = TimeValue.ofSeconds(15),
                        timeToLive = TimeValue.ofMinutes(15)
                    )
                }
            }
            setTlsConfigResolver { host ->
                log.debug { "scheme name=${host.schemeName}" }
                if (host.schemeName.contentEquals("https", ignoreCase = true)) {
                    tlsConfigOf(
                        supportedProtocols = listOf(TLS.V_1_0, TLS.V_1_1, TLS.V_1_2, TLS.V_1_3),
                        handshakeTimeout = Timeout.ofSeconds(60),
                    )
                } else {
                    TlsConfig.DEFAULT
                }
            }
        }

        httpAsyncClientOf(cm).use { client ->
            client.start()

            URIScheme.values().forEach { uriSchme: URIScheme ->
                val request = simpleHttpRequestOf(
                    method = Method.GET,
                    host = HttpHost(uriSchme.id, "httpbin.org"),
                    path = "/headers"
                )
                log.debug { "Executing request $request" }

                val response = client
                    .execute(
                        simpleRequestProducerOf(request),
                        SimpleResponseConsumer.create(),
                        null
                    )
                    .awaitSuspending()

                log.debug { "$request -> ${StatusLine(response)}" }
                log.debug { response.body }
            }
        }
    }
}
