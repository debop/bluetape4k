package io.bluetape4k.http.hc5.http

import org.apache.hc.core5.http.ClassicHttpRequest
import org.apache.hc.core5.http.Method
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder

inline fun classicRequest(
    methodName: String,
    initializer: ClassicRequestBuilder.() -> Unit,
): ClassicHttpRequest {
    return ClassicRequestBuilder.create(methodName).apply(initializer).build()
}

inline fun classicRequest(
    method: Method,
    initializer: ClassicRequestBuilder.() -> Unit,
): ClassicHttpRequest {
    return ClassicRequestBuilder.create(method.name).apply(initializer).build()
}
