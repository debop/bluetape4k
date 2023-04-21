package io.bluetape4k.io.retrofit2.client

import io.bluetape4k.io.compressor.Compressors
import io.bluetape4k.io.http.okhttp3.mock.baseUrl
import io.bluetape4k.io.retrofit2.AbstractRetrofitTest
import io.bluetape4k.io.retrofit2.retrofitOf
import io.bluetape4k.io.retrofit2.service
import io.bluetape4k.logging.KLogging
import io.bluetape4k.support.toUtf8Bytes
import okhttp3.mockwebserver.MockWebServer
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

abstract class AbstractClientTest: AbstractRetrofitTest() {

    companion object: KLogging()

    protected lateinit var server: MockWebServer
    protected lateinit var api: TestInterface

    protected abstract val callFactory: okhttp3.Call.Factory

    @BeforeEach
    fun setup() {
        server = MockWebServer()
        api = retrofitOf(server.baseUrl, callFactory).service()
    }

    @Test
    fun `verify api instance`() {
        api.shouldNotBeNull()
    }

    interface TestInterface {

        @POST("/?foo=bar&foo=baz&qux=")
        @Headers("Foo: Bar", "Foo: Baz", "Qux: ", "Content-Type: text/plain")
        fun post(body: String): Call<String>

        @POST("/path/{to}/resource")
        @Headers("Accept: text/plain")
        fun post(@Path("to") to: String?, body: String?): Call<String>

        @POST("/?foo=bar&foo=baz&qux=")
        @Headers("Foo: Bar", "Foo: Baz", "Qux: ", "Content-Type: text/plain")
        fun postForString(body: String?): String?

        @GET
        @Headers("Accept: text/plain", "Accept-Encoding: gzip, deflate, lz4, snappy, zstd")
        fun get(): Call<String>

        @GET("/?foo={multiFoo}")
        fun get(@Query("multiFoo") multiFoo: List<String?>?): Call<String>

        @Headers("Authorization: {authorization}")
        @GET
        fun getWithHeaders(@Header("authorization") authorization: String?): Call<String>

        @GET(value = "/?foo={multiFoo}")
        fun getCSV(@Query("multiFoo") multiFoo: List<String?>?): Call<String>

        @PATCH
        @Headers("Accept: text/plain")
        fun patch(body: String?): String?

        @POST
        fun noPostBody(): Call<String>

        @PUT
        fun noPutBody(): Call<String>

        @PATCH
        fun noPatchBody(): Call<String>

        @POST("/?foo=bar&foo=baz&qux=")
        @Headers("Foo: Bar", "Foo: Baz", "Qux: ")
        fun postWithContentType(body: String?, @Header("Content-Type") contentType: String): Call<String>
    }

    private fun gzip(data: String): ByteArray {
        return Compressors.GZip.compress(data.toUtf8Bytes())
    }

    private fun deflate(data: String): ByteArray {
        return Compressors.Deflate.compress(data.toUtf8Bytes())
    }
}
