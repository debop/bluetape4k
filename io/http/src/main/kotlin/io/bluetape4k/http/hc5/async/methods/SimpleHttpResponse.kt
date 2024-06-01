package io.bluetape4k.http.hc5.async.methods

import org.apache.hc.client5.http.async.methods.SimpleHttpResponse
import org.apache.hc.client5.http.async.methods.SimpleResponseBuilder
import org.apache.hc.core5.http.ContentType

inline fun simpleHttpResponse(
    status: Int,
    initializer: SimpleResponseBuilder.() -> Unit,
): SimpleHttpResponse {
    return SimpleResponseBuilder.create(status).apply(initializer).build()
}

fun simpleHttpResponseOf(
    code: Int,
    content: String,
    contentType: ContentType = ContentType.TEXT_PLAIN,
): SimpleHttpResponse {
    return SimpleHttpResponse.create(code, content, contentType)
}

fun simpleHttpResponseOf(
    code: Int,
    content: ByteArray,
    contentType: ContentType = ContentType.TEXT_PLAIN,
): SimpleHttpResponse {
    return SimpleHttpResponse.create(code, content, contentType)
}
