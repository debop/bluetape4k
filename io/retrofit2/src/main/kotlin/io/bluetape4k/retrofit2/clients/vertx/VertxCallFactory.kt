package io.bluetape4k.retrofit2.clients.vertx

import io.bluetape4k.http.vertx.defaultVertxHttpClient
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.logging.trace
import io.bluetape4k.logging.warn
import io.bluetape4k.retrofit2.toIOException
import io.vertx.core.http.HttpClient
import io.vertx.kotlin.core.http.requestOptionsOf
import kotlinx.atomicfu.atomic
import okhttp3.Call
import okhttp3.Request
import okio.Timeout
import java.time.Duration
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit

fun vertxCallFactoryOf(client: HttpClient = defaultVertxHttpClient): VertxCallFactory {
    return VertxCallFactory(client)
}

/**
 * Retrofit2 를 사용하기 위해, Http 통신을 Vertx의 [HttpClient]를 사용하도록 하는 Call.Factory 입니다.
 *
 * @property client Vertx [HttpClient] 인스턴스
 */
class VertxCallFactory private constructor(
    private val client: HttpClient,
): Call.Factory {

    companion object: KLogging() {
        val callTimeout: Duration = Duration.ofSeconds(30L)

        @JvmStatic
        operator fun invoke(client: HttpClient): VertxCallFactory {
            return VertxCallFactory(client)
        }
    }

    override fun newCall(request: Request): Call {
        return VertxCall(request)
    }

    /**
     * Vertx의 [HttpClient]를 사용하여, Http 통신을 수행하는 Call 입니다.
     *
     * @property okRequest OkHttp의 [Request] 인스턴스
     */
    private inner class VertxCall(
        private val okRequest: okhttp3.Request,
    ): okhttp3.Call {

        private val promiseRef = atomic<CompletableFuture<okhttp3.Response>?>(null)
        private val timeout = Timeout().timeout(callTimeout.toMillis(), TimeUnit.MILLISECONDS)

        override fun execute(): okhttp3.Response {
            log.debug { "Execute VertxCall. request=$okRequest" }

            return try {
                executeAsync().get(callTimeout.toMillis(), TimeUnit.MILLISECONDS)
            } catch (e: ExecutionException) {
                throw (e.cause ?: e).toIOException()
            } catch (e: Exception) {
                throw e.toIOException()
            }
        }

        override fun enqueue(responseCallback: okhttp3.Callback) {
            log.debug { "Enqueue VertxCall. request=$okRequest" }

            executeAsync()
                .thenApply { response -> responseCallback.onResponse(this, response) }
                .exceptionally { ex -> responseCallback.onFailure(this, ex.toIOException()) }
        }

        private fun executeAsync(): CompletableFuture<okhttp3.Response> {
            if (promiseRef.value != null) {
                throwAlreadyExecuted()
            }
            val promise = CompletableFuture<okhttp3.Response>()
            if (!promiseRef.compareAndSet(null, promise)) {
                throwAlreadyExecuted()
            }

            val options = requestOptionsOf(
                followRedirects = true,
                absoluteURI = okRequest.url.toString(),
            )

            client.request(options)
                .onSuccess { clientRequest ->
                    val vertxRequest = okRequest.toVertxHttpClientRequest(clientRequest)
                    log.trace { "Send vertx request ... request=$vertxRequest, version=${vertxRequest.version()}" }

                    vertxRequest.send()
                        .onSuccess { vertxResponse ->
                            vertxResponse.toOkResponse(okRequest, promise)
                        }
                        .onFailure { error ->
                            promise.completeExceptionally(error.toIOException())
                        }
                }
                .onFailure { error ->
                    promise.completeExceptionally(error.toIOException())
                }

            return promise
        }

        override fun isExecuted(): Boolean {
            return promiseRef.value?.isDone ?: false
        }

        override fun cancel() {
            promiseRef.value?.let { promise ->
                if (!promise.cancel(true)) {
                    log.warn { "Cannot cancel promise. $promise" }
                }
            }
        }

        override fun isCanceled(): Boolean {
            return promiseRef.value?.isCancelled ?: false
        }

        override fun clone(): okhttp3.Call {
            return VertxCall(okRequest)
        }

        override fun request(): okhttp3.Request {
            return okRequest
        }

        override fun timeout(): Timeout {
            return timeout
        }

        private fun throwAlreadyExecuted() {
            error("Already executed. request=$okRequest")
        }
    }
}
