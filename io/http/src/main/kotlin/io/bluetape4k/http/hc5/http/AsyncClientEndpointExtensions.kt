package io.bluetape4k.http.hc5.http

import io.bluetape4k.coroutines.support.coAwait
import org.apache.hc.client5.http.async.methods.SimpleHttpRequest
import org.apache.hc.client5.http.async.methods.SimpleHttpResponse
import org.apache.hc.client5.http.async.methods.SimpleResponseConsumer
import org.apache.hc.core5.concurrent.FutureCallback
import org.apache.hc.core5.http.nio.AsyncClientEndpoint
import org.apache.hc.core5.http.nio.AsyncRequestProducer
import org.apache.hc.core5.http.nio.AsyncResponseConsumer

suspend fun AsyncClientEndpoint.executeSuspending(request: SimpleHttpRequest): SimpleHttpResponse {
    return execute(request.toProducer(), SimpleResponseConsumer.create(), null).coAwait()
}

suspend fun <T: Any> AsyncClientEndpoint.executeSuspending(
    requestProducer: AsyncRequestProducer,
    responseConsumer: AsyncResponseConsumer<T>,
    callback: FutureCallback<T>? = null,
): T {
    return execute(requestProducer, responseConsumer, callback).coAwait()
}
