package io.bluetape4k.io.feign.clients.vertx

import feign.Request
import feign.Request.Options
import io.bluetape4k.io.feign.feignResponseBuilder
import io.bluetape4k.logging.KotlinLogging
import io.bluetape4k.logging.error
import io.bluetape4k.logging.trace
import io.bluetape4k.support.isNullOrEmpty
import io.vertx.core.http.HttpClient
import io.vertx.core.http.HttpClientRequest
import io.vertx.core.http.HttpClientResponse
import io.vertx.core.http.HttpMethod
import io.vertx.core.http.RequestOptions
import java.util.concurrent.CompletableFuture

private val log = KotlinLogging.logger { }

internal inline fun requestOptions(intializer: RequestOptions.() -> Unit): RequestOptions {
    return RequestOptions().apply(intializer)
}

internal fun Options.toVertxRequestOptions(feignRequest: feign.Request): RequestOptions {
    val self = this
    return requestOptions {
        followRedirects = self.isFollowRedirects
        timeout = self.readTimeoutMillis().toLong()
        method = HttpMethod.valueOf(feignRequest.httpMethod().name)
        setAbsoluteURI(feignRequest.url())
    }
}

internal fun HttpClientRequest.parseFromFeignRequest(feignRequest: feign.Request) {
    headers().add("accept", "*/*")
    headers().add("user-agent", "VertxHttpClient/4.4")
    headers().add("accept-encoding", "gzip,deflate")

    feignRequest.headers().forEach { (name, values) ->
        headers().add(name, values)
    }

    if (feignRequest.body().isNullOrEmpty()) {
        headers().add("content-length", "0")
    } else {
        write(io.vertx.core.buffer.Buffer.buffer(feignRequest.body()))
    }
}

internal fun HttpClientResponse.convertToFeignResponse(
    feignRequest: feign.Request,
    responsePromise: CompletableFuture<feign.Response>,
) {
    val self = this
    body { ar ->
        if (ar.succeeded()) {
            log.trace { "Convert Vertx HttpClientResponse to Feign Response. " }
            val buffer = ar.result()

            val headers = with(self.headers()) {
                names().associateWith { getAll(it) }
            }.toMap()

            val builder = feignResponseBuilder {
                protocolVersion(Request.ProtocolVersion.valueOf(self.version().name))
                request(feignRequest)
                status(self.statusCode())
                reason(self.statusMessage())
                headers(headers)
                body(buffer.bytes)
            }
            responsePromise.complete(builder.build())
        } else {
            responsePromise.completeExceptionally(ar.cause())
        }
    }
}

/**
 * Vertx [HttpClient]를 이용하여 Feign 의 [Request] 를 Async/Non-Blocking 방식으로 요청하고 응답을 받습니다.
 *
 * @param feignRequest [feign.Request] 인스턴스
 * @param feignOptions [feign.Request.Options] 인스턴스
 * @return [feign.Response]를 담은 [CompletableFuture] 인스턴스
 */
internal fun HttpClient.sendAsync(
    feignRequest: feign.Request,
    feignOptions: feign.Request.Options,
): CompletableFuture<feign.Response> {
    val options = feignOptions.toVertxRequestOptions(feignRequest)
    val promise = CompletableFuture<feign.Response>()

    this.request(options) { ar ->
        if (ar.succeeded()) {
            log.trace { "Build vertx httpclient request ..." }
            val vertxRequest = ar.result().apply { parseFromFeignRequest(feignRequest) }

            log.trace { "Send vertx httpclient request ..." }
            vertxRequest.send { ar2 ->
                if (ar2.succeeded()) {
                    log.trace { "Build feign response ..." }
                    ar2.result().convertToFeignResponse(feignRequest, promise)
                } else {
                    log.error(ar2.cause()) { "Fail to send vertx httpclient request." }
                    promise.completeExceptionally(ar2.cause())
                }
            }
        } else {
            log.error(ar.cause()) { "Fail to build vertx httpclient request." }
            promise.completeExceptionally(ar.cause())
        }
    }

    return promise
}
