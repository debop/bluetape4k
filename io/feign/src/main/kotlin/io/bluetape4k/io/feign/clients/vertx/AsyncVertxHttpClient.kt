package io.bluetape4k.io.feign.clients.vertx

import feign.AsyncClient
import feign.Request
import feign.Response
import io.bluetape4k.io.http.vertx.vertxHttpClientOf
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.vertx.toCompletableFuture
import io.vertx.core.http.HttpClient
import java.io.Closeable
import java.util.Optional
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

class AsyncVertxHttpClient private constructor(
    private val vertxClient: HttpClient,
): AsyncClient<Any>, Closeable {

    companion object: KLogging() {

        @JvmStatic
        operator fun invoke(vertxClient: HttpClient = vertxHttpClientOf()): AsyncVertxHttpClient {
            return AsyncVertxHttpClient(vertxClient)
        }
    }

    override fun execute(
        feignRequest: feign.Request,
        feignOptions: Request.Options,
        requestContext: Optional<Any>,
    ): CompletableFuture<Response> {
        return vertxClient.sendRequest(feignRequest, feignOptions)
    }

    override fun close() {
        log.debug { "Close AsyncVertxHttpClient." }
        runCatching {
            vertxClient.close().toCompletableFuture().get(5, TimeUnit.SECONDS)
        }
    }
}
