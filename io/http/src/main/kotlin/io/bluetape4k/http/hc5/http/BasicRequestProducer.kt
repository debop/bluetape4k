package io.bluetape4k.http.hc5.http

import org.apache.hc.core5.http.Method
import org.apache.hc.core5.http.message.BasicHttpRequest
import org.apache.hc.core5.http.nio.AsyncEntityProducer
import org.apache.hc.core5.http.nio.support.BasicRequestProducer
import java.net.URI

fun BasicHttpRequest.toProducer(): BasicRequestProducer = basicRequestProducerOf(this)

fun basicRequestProducerOf(
    request: BasicHttpRequest,
    dataProducer: AsyncEntityProducer? = null,
): BasicRequestProducer {
    return BasicRequestProducer(request, dataProducer)
}

fun basicRequestProducerOf(
    method: Method,
    uri: URI,
    dataProducer: AsyncEntityProducer? = null,
): BasicRequestProducer {
    return BasicRequestProducer(method.name, uri, dataProducer)
}

fun basicRequestProducerOf(
    methodName: String,
    uri: URI,
    dataProducer: AsyncEntityProducer? = null,
): BasicRequestProducer {
    return BasicRequestProducer(methodName, uri, dataProducer)
}
