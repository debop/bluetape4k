package io.bluetape4k.openai.client.interceptor

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.trace
import okhttp3.Interceptor
import okhttp3.Response

/**
 * OpenAI API Token을 Header에 추가하는 OkHttp Interceptor
 */
class AuthInterceptor(private val apiToken: String): Interceptor {

    companion object: KLogging()

    override fun intercept(chain: Interceptor.Chain): Response {
        log.trace { "Add Authorization header to request. apiToken=$apiToken" }

        val request = chain.request()
            .newBuilder()
            .header("Authorization", "Bearer $apiToken")
            .build()

        return chain.proceed(request)
    }
}
