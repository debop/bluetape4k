package io.bluetape4k.retrofit2.clients.hc5

import io.bluetape4k.http.hc5.async.httpAsyncClientSystemOf
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.retrofit2.toIOException
import kotlinx.atomicfu.atomic
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request
import okio.Timeout
import org.apache.hc.client5.http.async.methods.SimpleHttpResponse
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient
import org.apache.hc.core5.concurrent.FutureCallback
import java.io.IOException
import java.time.Duration
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit

fun hc5CallFactoryOf(asyncClient: CloseableHttpAsyncClient = httpAsyncClientSystemOf()): Hc5CallFactory {
    return Hc5CallFactory(asyncClient)
}

class Hc5CallFactory private constructor(
    private val asyncClient: CloseableHttpAsyncClient,
): okhttp3.Call.Factory {

    companion object: KLogging() {
        @JvmStatic
        val CallTimeout: Duration = Duration.ofSeconds(30L)

        @JvmStatic
        operator fun invoke(asyncClient: CloseableHttpAsyncClient): Hc5CallFactory {
            return Hc5CallFactory(asyncClient)
        }
    }

    init {
        // NOTE: 먼저 start() 를 호출해주어야 합니다.
        asyncClient.start()
    }

    override fun newCall(request: okhttp3.Request): okhttp3.Call {
        return AsyncClientCall(request)
    }

    private inner class AsyncClientCall(
        private val okRequest: okhttp3.Request,
        private val callTimeout: Duration = CallTimeout,
    ): okhttp3.Call {

        private val promiseRef = atomic<CompletableFuture<okhttp3.Response>?>(null)
        private val timeout = Timeout().timeout(callTimeout.toMillis(), TimeUnit.MILLISECONDS)

        override fun execute(): okhttp3.Response {
            log.debug { "Execute Hc5Call. request=$okRequest" }

            return try {
                // execute 는 Async 이지만 Blocking 입니다.
                executeAsync().get(callTimeout.toMillis(), TimeUnit.MILLISECONDS)
            } catch (e: ExecutionException) {
                throw (e.cause ?: e).toIOException()
            } catch (e: Exception) {
                throw e.toIOException()
            }
        }

        override fun enqueue(responseCallback: Callback) {
            log.debug { "Enqueue Hc5Call. request=$okRequest" }

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

            val simpleRequest = okRequest.toSimpleHttpRequest()

            asyncClient.execute(simpleRequest, object: FutureCallback<SimpleHttpResponse> {
                override fun completed(result: SimpleHttpResponse) {
                    try {
                        val okResponse: okhttp3.Response = result.toOkHttp3Response(okRequest)
                        promise.complete(okResponse)
                    } catch (e: Exception) {
                        promise.completeExceptionally(e.toIOException())
                    }
                }

                override fun failed(ex: java.lang.Exception) {
                    promise.completeExceptionally(IOException("Fail to execute. request=$okRequest", ex))
                }

                override fun cancelled() {
                    promise.completeExceptionally(IOException("Cancelled. request=$okRequest"))
                }
            })

            return promise
        }

        override fun isExecuted(): Boolean {
            return promiseRef.value?.isDone ?: false
        }

        override fun cancel() {
            promiseRef.value?.let { promise ->
                if (!promise.cancel(true)) {
                    log.warn("Cannot cancel promise. $promise")
                }
            }
        }

        override fun isCanceled(): Boolean {
            return promiseRef.value?.isCancelled ?: false
        }

        override fun clone(): Call {
            return AsyncClientCall(okRequest)
        }

        override fun request(): Request {
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
