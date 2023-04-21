package io.bluetape4k.io.retrofit2.clients.vertx

import io.bluetape4k.io.http.okhttp3.okhttp3Response
import io.bluetape4k.logging.KotlinLogging
import io.bluetape4k.logging.trace
import io.vertx.core.http.HttpClientRequest
import io.vertx.core.http.HttpClientResponse
import io.vertx.core.http.HttpMethod
import java.io.IOException
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Protocol
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Buffer

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
    okRequest.body?.let { body ->
        if (body.contentLength() > 0) {
            log.trace { "Build Vertx HttpClientRequest body..." }
            val contentType = body.contentType()
            putHeader("Content-Type", contentType.toString())
            putHeader("Content-Length", body.contentLength().toString())

            val buffer = Buffer()
            body.writeTo(buffer)
            write(io.vertx.core.buffer.Buffer.buffer(buffer.readByteArray()))
        }
    }
}

internal fun HttpClientResponse.toOkhttp3Response(okRequest: okhttp3.Request): Result<okhttp3.Response> {
    val self: HttpClientResponse = this
    var result: Result<okhttp3.Response>? = null
    body { ar ->
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
                result = Result.success(response)
            }

            else -> result = Result.failure(IOException(ar.cause()))
        }
    }.end().result()

    return result!!
}
