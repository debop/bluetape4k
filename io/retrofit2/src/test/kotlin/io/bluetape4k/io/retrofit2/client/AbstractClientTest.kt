package io.bluetape4k.io.retrofit2.client

import io.bluetape4k.io.compressor.Compressors
import io.bluetape4k.io.http.okhttp3.mock.baseUrl
import io.bluetape4k.io.retrofit2.AbstractRetrofitTest
import io.bluetape4k.io.retrofit2.retrofitOf
import io.bluetape4k.io.retrofit2.service
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.support.toUtf8Bytes
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import retrofit2.Call
import retrofit2.http.Body
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

    @BeforeAll
    fun beforeAll() {
        server = MockWebServer()
        api = retrofitOf(server.baseUrl, callFactory).service()
    }

    @AfterAll
    fun afterAll() {
        server.shutdown()
    }

    @Test
    fun `verify api instance`() {
        api.shouldNotBeNull()
    }

    @Disabled("테스트 중")
    @Test
    fun `run patch method`() {
        // server.enqueueBody("""{ "name": "foo" }""", "Content-Type: text/plain")
        server.enqueue(MockResponse().setBody("foo").addHeader("Content-Type", "text/plain"))

        val body = api.patch("").execute().body()
        log.debug { "body=$body" }
        body shouldBeEqualTo "foo"

        val request = server.takeRequest()
        request.headers["Accept"] shouldBeEqualTo "text/plain"
    }

    interface TestInterface {

        @POST("/?foo=bar&foo=baz&qux=")
        @Headers("Foo: Bar", "Foo: Baz", "Qux: ", "Content-Type: text/plain")
        fun post(@Body body: String): Call<String>

        @POST("/path/{to}/resource")
        @Headers("Accept: text/plain")
        fun post(@Path("to") to: String?, body: String?): Call<String>

        @POST("/?foo=bar&foo=baz&qux=")
        @Headers("Foo: Bar", "Foo: Baz", "Qux: ", "Content-Type: text/plain")
        fun postForString(@Body body: String?): Call<String>

        @GET
        @Headers("Accept: text/plain", "Accept-Encoding: gzip, deflate, lz4, snappy, zstd")
        fun get(): Call<String>

        @GET("/?foo={multiFoo}")
        fun get(@Query("multiFoo") multiFoo: List<String?>?): Call<String>

        @GET
        fun getWithHeaders(@Header("Authorization") authorization: String?): Call<String>

        @GET(value = "/?foo={multiFoo}")
        fun getCSV(@Query("multiFoo") multiFoo: List<String?>?): Call<String>

        @PATCH("/")
        @Headers("Accept: text/plain", "Content-Type: text/plain")
        fun patch(@Body body: String?): Call<String>

        @POST("/")
        fun noPostBody(): Call<String>

        @PUT
        fun noPutBody(): Call<String>

        @PATCH
        fun noPatchBody(): Call<String>

        @POST("/?foo=bar&foo=baz&qux=")
        @Headers("Foo: Bar", "Foo: Baz", "Qux: ")
        fun postWithContentType(@Body body: String?, @Header("Content-Type") contentType: String): Call<String>
    }

    private fun gzip(data: String): ByteArray {
        return Compressors.GZip.compress(data.toUtf8Bytes())
    }

    private fun deflate(data: String): ByteArray {
        return Compressors.Deflate.compress(data.toUtf8Bytes())
    }
}
