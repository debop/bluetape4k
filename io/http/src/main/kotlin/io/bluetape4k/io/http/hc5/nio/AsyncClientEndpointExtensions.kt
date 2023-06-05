package io.bluetape4k.io.http.hc5.nio

import io.bluetape4k.coroutines.support.awaitSuspending
import io.bluetape4k.io.http.hc5.async.methods.toProducer
import org.apache.hc.client5.http.async.methods.SimpleHttpRequest
import org.apache.hc.client5.http.async.methods.SimpleHttpResponse
import org.apache.hc.client5.http.async.methods.SimpleResponseConsumer
import org.apache.hc.core5.concurrent.FutureCallback
import org.apache.hc.core5.http.nio.AsyncClientEndpoint
import org.apache.hc.core5.http.nio.AsyncRequestProducer
import org.apache.hc.core5.http.nio.AsyncResponseConsumer

suspend fun AsyncClientEndpoint.executeSuspending(request: SimpleHttpRequest): SimpleHttpResponse {
    return execute(request.toProducer(), SimpleResponseConsumer.create(), null).awaitSuspending()
}

suspend fun <T: Any> AsyncClientEndpoint.executeSuspending(
    requestProducer: AsyncRequestProducer,
    responseConsumer: AsyncResponseConsumer<T>,
    callback: FutureCallback<T>? = null,
): T {
    return execute(requestProducer, responseConsumer, callback).awaitSuspending()
}
