package io.bluetape4k.io.http.okhttp3

import io.bluetape4k.logging.KLogging
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.Response

/**
 * OkHttp3 [Response]에 캐싱 설정을 추가하는 Interceptor 입니다.
 *
 * OkHttp3Client Builder에 `addNetworkInterceptor` 에 추가해야 합니다.
 *
 * ```
 * client.addNetworkInterceptor(CachingResponseInterceptor())
 * ```
 */
class CachingResponseInterceptor private constructor(
    private val cacheControl: CacheControl,
): Interceptor {

    companion object: KLogging() {
        operator fun invoke(
            cacheControl: CacheControl = okhttp3CacheControlOf(),
        ): CachingResponseInterceptor {
            return CachingResponseInterceptor(cacheControl)
        }

        operator fun invoke(
            maxAgeInSeconds: Int = 0,
            maxStaleInSeconds: Int = 0,
            minFreshInSeconds: Int = 0,
            onlyIfCached: Boolean = false,
            noCache: Boolean = false,
            noStore: Boolean = false,
            noTransform: Boolean = false,
            immutable: Boolean = false,
        ): CachingResponseInterceptor {
            val cacheControl = okhttp3CacheControlOf(
                maxAgeInSeconds = maxAgeInSeconds,
                maxStaleInSeconds = maxStaleInSeconds,
                minFreshInSeconds = minFreshInSeconds,
                onlyIfCached = onlyIfCached,
                noCache = noCache,
                noStore = noStore,
                noTransform = noTransform,
                immutable = immutable
            )
            return CachingResponseInterceptor(cacheControl)
        }
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())

        return when {
            response.header("Cache-Control").isNullOrBlank() ->
                response.newBuilder()
                    .header("Cache-Control", cacheControl.toString())
                    .build()

            else -> response
        }
    }
}
