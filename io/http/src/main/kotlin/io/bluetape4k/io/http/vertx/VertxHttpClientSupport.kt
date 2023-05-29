package io.bluetape4k.io.http.vertx

import io.bluetape4k.vertx.currentVertx
import io.vertx.core.http.HttpClient
import io.vertx.core.http.HttpClientOptions
import io.vertx.kotlin.core.http.httpClientOptionsOf

@JvmField
val defaultVertxHttpClientOptions: HttpClientOptions = httpClientOptionsOf(
    protocolVersion = HttpClientOptions.DEFAULT_PROTOCOL_VERSION,
    useAlpn = true,
    trustAll = true,
    logActivity = true,
    tryUseCompression = true,
    tryUsePerFrameWebSocketCompression = true,
    tryUsePerMessageWebSocketCompression = true
)

val defaultVertxHttpClient: HttpClient by lazy(mode = LazyThreadSafetyMode.PUBLICATION) {
    vertxHttpClientOf(defaultVertxHttpClientOptions)
}

fun vertxHttpClientOf(options: HttpClientOptions = defaultVertxHttpClientOptions): HttpClient {
    return currentVertx().createHttpClient(options)
}
