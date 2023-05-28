package io.bluetape4k.io.retrofit2.clients.vertx

import io.bluetape4k.io.http.okhttp3.okhttp3Response
import io.bluetape4k.logging.KotlinLogging
import io.bluetape4k.logging.trace
import io.vertx.core.AsyncResult
import io.vertx.core.http.HttpClientRequest
import io.vertx.core.http.HttpClientResponse
import io.vertx.core.http.HttpMethod
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Protocol
import okhttp3.RequestBody
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Buffer
import java.io.IOException

private val log = KotlinLogging.logger {}

internal fun HttpClientRequest.parse(okRequest: okhttp3.Request) {
    putHeader("accept", "*/*")
    putHeader("user-agent", "VertxHttpClient/4.4")
    putHeader("accept-encoding", "gzip")

    okRequest.headers.forEach { (name, value) ->
        log.trace { "Add header. $name=$value" }
        headers().add(name, value)
    }
    method = HttpMethod.valueOf(okRequest.method)

    // Build Vertx HttpClientRequest Body
    okRequest.body?.let { body: RequestBody ->
        if (body.contentLength() > 0) {
            log.trace { "Build Vertx HttpClientRequest body..." }
            val contentType = body.contentType()
            putHeader("Content-Type", contentType.toString())
            putHeader("Content-Length", body.contentLength().toString())

            val buffer = Buffer()
            body.writeTo(buffer)
            write(io.vertx.core.buffer.Buffer.buffer(buffer.readByteArray()))
        } else {
            putHeader("Content-Length", "0")
        }
    } ?: run {
        putHeader("Content-Length", "0")
    }
}

internal fun HttpClientResponse.toOkhttp3Response(
    call: okhttp3.Call,
    okRequest: okhttp3.Request,
    callback: okhttp3.Callback,
) {
    val self: HttpClientResponse = this
    body { ar: AsyncResult<io.vertx.core.buffer.Buffer> ->
        when {
            ar.succeeded() -> {
                log.trace { "Convert Vertx HttpClientResponse to okhttp3.Response." }

                val response = okhttp3Response {
                    protocol(Protocol.valueOf(self.version().name))
                    request(okRequest)
                    code(self.statusCode())
                    message(self.statusMessage())

                    self.headers().forEach { (key, value) ->
                        log.trace { "Response header key=$key, value=$value" }
                        addHeader(key, value)
                    }
                    val contentTypeHeader = self.getHeader("content-type")
                    val contentType = contentTypeHeader?.toMediaTypeOrNull()

                    val buffer = ar.result()
                    body(buffer.bytes.toResponseBody(contentType))
                }
                callback.onResponse(call, response)
            }

            else           -> callback.onFailure(call, IOException(ar.cause()))
        }
    }
}
