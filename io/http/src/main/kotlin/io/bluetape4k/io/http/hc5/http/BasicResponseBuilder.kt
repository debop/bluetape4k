package io.bluetape4k.io.http.hc5.http

import org.apache.hc.core5.http.HttpResponse
import org.apache.hc.core5.http.message.BasicHttpResponse
import org.apache.hc.core5.http.support.BasicResponseBuilder

inline fun basicHttpResponse(
    status: Int,
    initializer: BasicResponseBuilder.() -> Unit,
): BasicHttpResponse {
    return BasicResponseBuilder.create(status).apply(initializer).build()
}

fun basicHttpResponse(
    response: HttpResponse,
    initializer: BasicResponseBuilder.() -> Unit,
): BasicHttpResponse {
    return BasicResponseBuilder.copy(response).apply(initializer).build()
}
