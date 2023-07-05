package io.bluetape4k.openai.clients.retrofit2

import okhttp3.Interceptor
import okhttp3.Response

class AuthenticationInterceptor(private val token: String): Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
            .newBuilder()
            .header("Authorization", "Bearer $token")
            .build()

        return chain.proceed(request)
    }
}
