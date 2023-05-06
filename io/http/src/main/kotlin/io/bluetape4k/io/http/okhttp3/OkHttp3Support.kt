package io.bluetape4k.io.http.okhttp3

import io.bluetape4k.utils.Runtimex
import okhttp3.CacheControl
import okhttp3.Callback
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.io.InputStream
import java.time.Duration
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

fun okHttp3ConnectionPool(
    maxIdleConnections: Int = Runtimex.availableProcessors,
    keepAliveDurations: Duration = Duration.ofMinutes(5),
): ConnectionPool {
    return ConnectionPool(maxIdleConnections, keepAliveDurations.toSeconds(), TimeUnit.SECONDS)
}

fun okhttp3ClientBuilderOf(
    connectionPool: ConnectionPool = okHttp3ConnectionPool(),
): OkHttpClient.Builder {
    return OkHttpClient.Builder()
        .apply {
            connectionPool(connectionPool)
            connectTimeout(Duration.ofSeconds(10))
            readTimeout(Duration.ofSeconds(10))
            writeTimeout(Duration.ofSeconds(30))
        }
}

inline fun okhttp3Client(
    connectionPool: ConnectionPool = okHttp3ConnectionPool(),
    initializer: OkHttpClient.Builder.() -> Unit,
): OkHttpClient {
    return okhttp3ClientBuilderOf(connectionPool)
        .apply(initializer)
        .build()
}

inline fun okhttp3CacheControl(
    initializer: CacheControl.Builder.() -> Unit,
): CacheControl {
    return CacheControl.Builder().apply(initializer).build()
}

fun okhttp3CacheControlOf(
    maxAgeInSeconds: Int = 0,
    maxStaleInSeconds: Int = 0,
    minFreshInSeconds: Int = 0,
    onlyIfCached: Boolean = false,
    noCache: Boolean = false,
    noStore: Boolean = false,
    noTransform: Boolean = false,
    immutable: Boolean = false,
): CacheControl {
    return okhttp3CacheControl {
        maxAge(maxAgeInSeconds, TimeUnit.SECONDS)
        maxStale(maxStaleInSeconds, TimeUnit.SECONDS)
        minFresh(minFreshInSeconds, TimeUnit.SECONDS)
        if (onlyIfCached) onlyIfCached()
        if (noCache) noCache()
        if (noStore) noStore()
        if (noTransform) noTransform()
        if (immutable) immutable()
    }
}

inline fun okhttp3Request(initializer: Request.Builder.() -> Unit): Request {
    return Request.Builder().apply(initializer).build()
}

fun okhttp3RequestOf(url: String, vararg nameAndValues: String): Request {
    return okhttp3Request {
        url(url)
        okhttp3.Headers.Companion.headersOf(*nameAndValues)
    }
}

fun okhttp3RequestOf(url: String, headers: okhttp3.Headers): Request {
    return okhttp3Request {
        url(url)
        headers(headers)
    }
}

inline fun okhttp3Response(initializer: okhttp3.Response.Builder.() -> Unit): Response {
    return Response.Builder().apply(initializer).build()
}

fun Response?.bodyAsInputStream(): InputStream? = this?.body?.byteStream()

fun Response?.bodyAsByteArray(): ByteArray? = this?.body?.bytes()

fun Response?.bodyAsString(): String? = this?.body?.string()

fun OkHttpClient.execute(request: Request): Response = newCall(request).execute()

/**
 * [OkHttpClient]를 비동기 방식으로 실행합니다. (단 CompletableFuture를 반환하므로, Non-Blocking 은 아닙니다)
 *
 * @param request [okhttp3.Request] 인스턴스
 * @param cancelHandler 취소된 경우에 호출할 handler
 * @receiver [OkHttpClient] 인스턴스
 * @return [okhttp3.Response]를 가지는 CompletableFuture 인스턴스
 */
inline fun OkHttpClient.executeAsync(
    request: Request,
    crossinline cancelHandler: (Throwable) -> Unit = {},
): CompletableFuture<Response> {
    val promise = CompletableFuture<Response>()

    val callback = object: Callback {
        override fun onResponse(call: okhttp3.Call, response: Response) {
            when {
                response.isSuccessful -> promise.complete(response)
                call.isCanceled()     -> handleCanceled(IOException("Canceled"))
                else                  -> handleCanceled(IOException("Unexpected code $response"))
            }
        }

        override fun onFailure(call: okhttp3.Call, e: java.io.IOException) {
            if (call.isCanceled()) {
                handleCanceled(e)
            } else {
                promise.completeExceptionally(e)
            }
        }

        private fun handleCanceled(e: IOException) {
            cancelHandler(e)
            promise.completeExceptionally(e)
        }
    }

    newCall(request).enqueue(callback)
    return promise
}

fun okhttp3.Response.print(no: Int = 1) {
    println("Response[$no]: ${this.code} ${this.message}")
    println("Headers[$no]: ${this.headers}")
    println("Cache Response[$no]: ${this.cacheResponse}")
    println("Network Response[$no]: ${this.networkResponse}")
}
