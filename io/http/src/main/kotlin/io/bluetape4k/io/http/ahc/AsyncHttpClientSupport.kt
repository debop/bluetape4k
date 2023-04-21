package io.bluetape4k.io.http.ahc

import io.bluetape4k.io.netty.isPresentNettyTransportNativeEpoll
import io.bluetape4k.io.netty.isPresentNettyTransportNativeKQueue
import io.bluetape4k.support.classIsPresent
import io.bluetape4k.utils.Systemx
import io.netty.channel.epoll.EpollEventLoopGroup
import io.netty.channel.kqueue.KQueueEventLoopGroup
import org.asynchttpclient.AsyncHttpClient
import org.asynchttpclient.AsyncHttpClientConfig
import org.asynchttpclient.DefaultAsyncHttpClientConfig
import org.asynchttpclient.Dsl
import org.asynchttpclient.filter.RequestFilter
import org.asynchttpclient.filter.ResponseFilter

// NOTE: 비동기 방식에서는 OS 차원에서 open file 제한을 늘려야 합니다.
// 참고 : https://gist.github.com/tombigel/d503800a282fcadbee14b537735d202c

val defaultAsyncHttpClientConfig: DefaultAsyncHttpClientConfig by lazy {
    DefaultAsyncHttpClientConfig.Builder().apply {
        runCatching {
            if (Systemx.isUnix && classIsPresent("io.netty.channel.epoll.EpollEventLoopGroup")) {
                setEventLoopGroup(EpollEventLoopGroup())
                setUseNativeTransport(true)

            } else if (Systemx.isMac && classIsPresent("io.netty.channel.kqueue.KQueueEventLoopGroup")) {
                setEventLoopGroup(KQueueEventLoopGroup())
                setUseNativeTransport(true)
            } else {
                // Nothing to do
            }
        }
    }
        .build()
}

/**
 * Default [AsyncHttpClient] instance
 */
val defaultAsyncHttpClient: AsyncHttpClient by lazy {
    Dsl.asyncHttpClient(defaultAsyncHttpClientConfig)
}

inline fun asyncHttpClientConfig(
    initializer: DefaultAsyncHttpClientConfig.Builder.() -> Unit,
): DefaultAsyncHttpClientConfig {
    return DefaultAsyncHttpClientConfig.Builder()
        .apply {
            setCompressionEnforced(true)
            setKeepAlive(true)
            setTcpNoDelay(true)
            setSoReuseAddress(true)
            setPooledConnectionIdleTimeout(120_000)  // 120 seconds (2 minutes)
            setFollowRedirect(true)
            setMaxRedirects(5)
            setMaxRequestRetry(3)


            // Netty native transport를 사용할 수 있으면 사용하도록 한다
            when {
                Systemx.isUnix -> {
                    if (isPresentNettyTransportNativeEpoll()) {
                        setEventLoopGroup(EpollEventLoopGroup())
                        setUseNativeTransport(true)
                    }
                }

                Systemx.isMac -> {
                    if (isPresentNettyTransportNativeKQueue()) {
                        setEventLoopGroup(KQueueEventLoopGroup())
                        setUseNativeTransport(true)
                    }
                }
            }
        }
        .apply(initializer)
        .build()
}


fun asyncHttpClientConfigOf(
    requestFilters: Collection<RequestFilter> = emptyList(),
    responseFilters: Collection<ResponseFilter> = emptyList(),
): DefaultAsyncHttpClientConfig =
    asyncHttpClientConfig {
        requestFilters.forEach { addRequestFilter(it) }
        responseFilters.forEach { addResponseFilter(it) }
    }

/**
 * 새로운 [AsyncHttpClient]를 생성합니다.
 *
 * ```
 * val ahc = asyncHttpClient {
 *      setCompressionEnforced(true)
 *      setKeeyAlive(true)
 *      setMaxRedirects(5)
 *      setMaxRequestRetry(3)
 * }
 * ```
 *
 * @param config methods of [DefaultAsyncHttpClientConfig.Builder] that customize resulting AsyncHttpClient
 * @return
 */
inline fun asyncHttpClient(config: DefaultAsyncHttpClientConfig.Builder.() -> Unit): AsyncHttpClient {
    val builder = DefaultAsyncHttpClientConfig.Builder().apply(config)
    return Dsl.asyncHttpClient(builder)
}


/**
 * RequestFilter들을 등록한 [AsyncHttpClient] 를 제공합니다.
 *
 * @param config [AsyncHttpClientConfig] instance
 * @return [AsyncHttpClient] instance
 */
fun asyncHttpClientOf(config: AsyncHttpClientConfig = defaultAsyncHttpClientConfig): AsyncHttpClient {
    return Dsl.asyncHttpClient(config)
}

/**
 * RequestFilter들을 등록한 [AsyncHttpClient] 를 제공합니다.
 *
 * @param requestFilters request filters
 * @return [AsyncHttpClient] instance
 */
fun asyncHttpClientOf(vararg requestFilters: RequestFilter): AsyncHttpClient {
    val config = asyncHttpClientConfigOf(requestFilters.toList())
    return Dsl.asyncHttpClient(config)
}
