package io.bluetape4k.retrofit2.clients.vertx

import io.bluetape4k.http.okhttp3.okhttp3Response
import io.bluetape4k.io.compressor.Compressors
import io.bluetape4k.logging.KotlinLogging
import io.bluetape4k.logging.trace
import io.bluetape4k.retrofit2.toIOException
import io.vertx.core.http.HttpClientRequest
import io.vertx.core.http.HttpClientResponse
import io.vertx.core.http.HttpHeaders
import io.vertx.core.http.HttpMethod
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Protocol
import okhttp3.RequestBody
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Buffer
import java.util.concurrent.CompletableFuture

private val log = KotlinLogging.logger {}

internal fun okhttp3.Request.toVertxHttpClientRequest(request: HttpClientRequest): HttpClientRequest {
    val self = this@toVertxHttpClientRequest

    request.putHeader(HttpHeaders.ACCEPT, "*/*")

    self.headers.forEach { (name, value) ->
        request.headers().add(name, value)
    }
    request.method = HttpMethod.valueOf(self.method)

    // Build Vertx HttpClientRequest Body
    self.body?.let { body: RequestBody ->
        if (body.contentLength() > 0) {
            val contentType = body.contentType()

            request.putHeader(HttpHeaders.CONTENT_TYPE, contentType.toString())
            request.putHeader(HttpHeaders.CONTENT_LENGTH, body.contentLength().toString())

            val buffer = Buffer()
            body.writeTo(buffer)
            request.write(io.vertx.core.buffer.Buffer.buffer(buffer.readByteArray()))
        } else {
            request.putHeader(HttpHeaders.CONTENT_LENGTH, "0")
        }
    } ?: run {
        request.putHeader(HttpHeaders.CONTENT_LENGTH, "0")
    }

    return request
}

internal fun io.vertx.core.http.HttpClientResponse.toOkResponse(
    okRequest: okhttp3.Request,
    promise: CompletableFuture<okhttp3.Response>,
) {
    val self: HttpClientResponse = this@toOkResponse

    body()
        .onSuccess { buffer ->
            log.trace { "Convert Vertx HttpClientResponse to okhttp3.Response. version=${self.version()}" }

            val response = okhttp3Response {
                protocol(Protocol.valueOf(self.version().name))
                request(okRequest)
                code(self.statusCode())
                message(self.statusMessage())

                log.trace { "protocol=${self.version().name}, code=${self.statusCode()}, message=${self.statusMessage()}" }

                self.headers().forEach { (key, value) ->
                    addHeader(key, value)
                }

                val contentEncoding = self.getHeader(HttpHeaders.CONTENT_ENCODING)?.lowercase() ?: ""

                val bytes = when (contentEncoding) {
                    "gzip"    -> Compressors.GZip.decompress(buffer.bytes)
                    "deflate" -> Compressors.Deflate.decompress(buffer.bytes)
                    else      -> buffer.bytes
                }

                val contentTypeHeader = self.getHeader(HttpHeaders.CONTENT_TYPE)
                val contentType = contentTypeHeader?.toMediaTypeOrNull()

                body(bytes.toResponseBody(contentType))
            }
            promise.complete(response)
        }
        .onFailure { error ->
            promise.completeExceptionally(error.toIOException())
        }
}
