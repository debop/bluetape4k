package io.bluetape4k.aws.http

import software.amazon.awssdk.http.async.SdkAsyncHttpClient
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient
import java.time.Duration

inline fun nettyNioAsyncHttpClient(
    initializer: NettyNioAsyncHttpClient.Builder.() -> Unit,
): SdkAsyncHttpClient {
    return NettyNioAsyncHttpClient.builder().apply(initializer).build()
}

fun nettyNioAsyncHttpClientOf(
    maxConcurrency: Int = 100,
    connectionMaxIdleTime: Duration = Duration.ofSeconds(30),
    connectionTimeout: Duration = Duration.ofSeconds(30),
    readTimeout: Duration = Duration.ofSeconds(30),
    writeTimeout: Duration = Duration.ofSeconds(30),
    initializer: NettyNioAsyncHttpClient.Builder.() -> Unit = {},
): SdkAsyncHttpClient = nettyNioAsyncHttpClient {
    this.maxConcurrency(maxConcurrency)
    this.connectionMaxIdleTime(connectionMaxIdleTime)
    this.connectionTimeout(connectionTimeout)
    this.readTimeout(readTimeout)
    this.writeTimeout(writeTimeout)

    initializer()
}
