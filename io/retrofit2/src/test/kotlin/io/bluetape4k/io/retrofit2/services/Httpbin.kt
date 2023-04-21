package io.bluetape4k.io.retrofit2.services

import io.bluetape4k.logging.KLogging
import retrofit2.Call
import retrofit2.http.GET

/**
 * [httpbin](https://httpbin.org/) 에서 제공하는 API를 테스트하기 위한 서비스 클래스입니다.
 */
object Httpbin: KLogging() {

    const val BASE_URL = "https://httpbin.org"

    interface HttpbinApi {

        @GET("/get")
        fun get(): Call<String>

    }

}
