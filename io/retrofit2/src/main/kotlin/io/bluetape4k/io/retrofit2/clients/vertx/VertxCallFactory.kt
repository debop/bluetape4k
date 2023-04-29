package io.bluetape4k.io.retrofit2.clients.vertx

import io.bluetape4k.concurrent.futureWithTimeout
import io.bluetape4k.io.http.vertx.defaultVertxHttpClient
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.logging.trace
import io.bluetape4k.logging.warn
import io.vertx.core.http.HttpClient
import io.vertx.kotlin.core.http.requestOptionsOf
import java.io.IOException
import java.time.Duration
import java.util.concurrent.CompletableFuture
import kotlinx.atomicfu.atomic
import okhttp3.Call
import okhttp3.Request
import okio.Timeout

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

        private val executed = atomic(false)
        private val cancelRequest = atomic(false)
        private val canceled = atomic(false)

        override fun execute(): okhttp3.Response {
            log.debug { "Execute VertxCall. request=$okRequest" }

            return try {
                val promise = CompletableFuture<okhttp3.Response>()
                futureWithTimeout(callTimeout.toMillis()) {
                    enqueue(object: okhttp3.Callback {
                        override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                            promise.complete(response)
                        }

                        override fun onFailure(call: okhttp3.Call, e: java.io.IOException) {
                            promise.completeExceptionally(e)
                        }
                    })
                }
                promise.get()
            } catch (e: Exception) {
                throw IOException(e)
            }
        }

        override fun enqueue(responseCallback: okhttp3.Callback) {
            if (!executed.compareAndSet(false, true)) {
                log.warn { "Already executed." }
                responseCallback.onFailure(this, java.io.IOException("Already executed."))
                return
            }

            val options = requestOptionsOf(
                followRedirects = true,
                absoluteURI = okRequest.url.toString(),
            )

            client.request(options) { ar ->
                if (ar.succeeded()) {
                    val request = ar.result().apply { parse(okRequest) }

                    log.trace { "Send vertx request ... request=$request" }
                    request.send { ar2 ->
                        if (ar2.succeeded()) {
                            val response = ar2.result()
                            response.toOkhttp3Response(this@VertxCall, okRequest, responseCallback)
                        } else {
                            responseCallback.onFailure(this@VertxCall, IOException(ar2.cause()))
                        }
                    }
                } else {
                    responseCallback.onFailure(this@VertxCall, IOException(ar.cause()))
                }
            }
        }

        override fun isExecuted(): Boolean {
            return executed.value
        }

        override fun cancel() {
            cancelRequest.value = true
            log.debug { "cancel request. request=$okRequest" }
        }

        override fun isCanceled(): Boolean {
            return canceled.value
        }

        override fun clone(): okhttp3.Call {
            return VertxCall(okRequest)
        }

        override fun request(): okhttp3.Request {
            return okRequest
        }

        override fun timeout(): Timeout {
            // TODO: 다른 놈은 어떻게 설정했는지 참고해서 수정하자 
            return Timeout.NONE
        }
    }
}
