package io.bluetape4k.http.hc5.async.methods

import org.apache.hc.client5.http.async.methods.SimpleHttpRequest
import org.apache.hc.client5.http.async.methods.SimpleRequestProducer

fun simpleRequestProducerOf(request: SimpleHttpRequest): SimpleRequestProducer {
    return SimpleRequestProducer.create(request)
}
