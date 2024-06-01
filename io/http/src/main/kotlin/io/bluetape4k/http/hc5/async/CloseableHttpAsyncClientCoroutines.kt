package io.bluetape4k.http.hc5.async

import io.bluetape4k.coroutines.support.coAwait
import io.bluetape4k.http.hc5.async.methods.toProducer
import org.apache.hc.client5.http.async.methods.SimpleHttpRequest
import org.apache.hc.client5.http.async.methods.SimpleHttpResponse
import org.apache.hc.client5.http.async.methods.SimpleResponseConsumer
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient
import org.apache.hc.client5.http.protocol.HttpClientContext
import org.apache.hc.core5.concurrent.FutureCallback
import org.apache.hc.core5.http.protocol.HttpContext

suspend fun CloseableHttpAsyncClient.executeSuspending(
    request: SimpleHttpRequest,
    responseConsumer: SimpleResponseConsumer = SimpleResponseConsumer.create(),
    context: HttpContext = HttpClientContext.create(),
    callback: FutureCallback<SimpleHttpResponse>? = null,
): SimpleHttpResponse {
    return execute(request.toProducer(), responseConsumer, context, callback).coAwait()
}
