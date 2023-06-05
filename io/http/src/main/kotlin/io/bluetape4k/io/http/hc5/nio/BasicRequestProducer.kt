package io.bluetape4k.io.http.hc5.nio

import org.apache.hc.core5.http.message.BasicHttpRequest
import org.apache.hc.core5.http.nio.AsyncEntityProducer
import org.apache.hc.core5.http.nio.support.BasicRequestProducer

fun BasicHttpRequest.toProducer(): BasicRequestProducer = basicRequestProducerOf(this)

fun basicRequestProducerOf(
    request: BasicHttpRequest,
    dataProducer: AsyncEntityProducer? = null,
): BasicRequestProducer {
    return BasicRequestProducer(request, dataProducer)
}
