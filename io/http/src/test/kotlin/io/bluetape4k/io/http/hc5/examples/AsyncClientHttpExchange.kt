package io.bluetape4k.io.http.hc5.examples

import io.bluetape4k.io.http.hc5.AbstractHc5Test
import io.bluetape4k.io.http.hc5.async.executeSuspending
import io.bluetape4k.io.http.hc5.async.methods.simpleHttpRequestOf
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.logging.error
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.test.runTest
import org.apache.hc.client5.http.async.methods.SimpleHttpResponse
import org.apache.hc.client5.http.async.methods.SimpleRequestBuilder
import org.apache.hc.client5.http.async.methods.SimpleRequestProducer
import org.apache.hc.client5.http.async.methods.SimpleResponseConsumer
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient
import org.apache.hc.client5.http.impl.async.HttpAsyncClients
import org.apache.hc.core5.concurrent.FutureCallback
import org.apache.hc.core5.http.HttpHost
import org.apache.hc.core5.http.Method
import org.apache.hc.core5.http.message.StatusLine
import org.apache.hc.core5.io.CloseMode
import org.apache.hc.core5.reactor.IOReactorConfig
import org.apache.hc.core5.util.Timeout
import org.junit.jupiter.api.Test

class AsyncClientHttpExchange: AbstractHc5Test() {

    companion object: KLogging()

    @Test
    fun `asynchronous HTTP 1_1 request execution`() {
        val target = HttpHost(httpbinServer.host, httpbinServer.port)
        val requestUris = listOf("/", "/ip", "/user-agent", "/headers")

        val ioReactorConfig = IOReactorConfig.custom()
            .setSoTimeout(Timeout.ofSeconds(5))
            .build()

        val client: CloseableHttpAsyncClient = HttpAsyncClients.custom()
            .setIOReactorConfig(ioReactorConfig)
            .build()

        // NOTE: 먼저 start() 를 호출해주어야 합니다.
        client.start()

        requestUris.forEach { requestUri ->
            val request = SimpleRequestBuilder.get()
                .setHttpHost(target)
                .setPath(requestUri)
                .build()

            log.debug { "Executing request $request" }

            val future = client.execute(
                SimpleRequestProducer.create(request),
                SimpleResponseConsumer.create(),
                object: FutureCallback<SimpleHttpResponse> {
                    override fun completed(result: SimpleHttpResponse) {
                        log.debug { "$request -> ${StatusLine(result)}" }
                        log.debug { result.body }
                    }

                    override fun failed(ex: Exception) {
                        log.error(ex) { "Fail to exchange. request=$request" }
                    }

                    override fun cancelled() {
                        log.debug { "Request is cancelled. request=$request" }
                    }
                }
            )
            future.get()
        }

        log.debug { "Shutting down" }
        client.close(CloseMode.GRACEFUL)
    }

    @Test
    fun `asynchronous HTTP 1_1 request in coroutines`() = runTest {
        val target = HttpHost(httpbinServer.host, httpbinServer.port)
        val requestUris = listOf("/", "/ip", "/user-agent", "/headers")

        val ioReactorConfig = IOReactorConfig.custom()
            .setSoTimeout(Timeout.ofSeconds(5))
            .build()

        val client: CloseableHttpAsyncClient = HttpAsyncClients.custom()
            .setIOReactorConfig(ioReactorConfig)
            .build()

        // NOTE: 먼저 start() 를 호출해주어야 합니다.
        client.start()

        val responses = requestUris.map { path ->
            val request = simpleHttpRequestOf(Method.GET, target, path)
            async(Dispatchers.IO) {
                client.executeSuspending(request)
                    .apply {
                        log.debug { "$request -> ${StatusLine(this)}" }
                        log.debug { this.body }
                    }
            }
        }

        responses.awaitAll()
        log.debug { "Shutting down" }
        client.close(CloseMode.GRACEFUL)
    }
}
