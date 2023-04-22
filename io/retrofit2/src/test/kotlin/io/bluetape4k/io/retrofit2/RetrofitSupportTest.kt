package io.bluetape4k.io.retrofit2

import io.bluetape4k.concurrent.allAsList
import io.bluetape4k.io.retrofit2.services.JsonPlaceHolder
import io.bluetape4k.io.retrofit2.services.JsonPlaceHolder.JsonPlaceHolderApi
import io.bluetape4k.junit5.coroutines.runSuspendWithIO
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.future.await
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.Test
import kotlin.random.Random

class RetrofitSupportTest: AbstractRetrofitTest() {

    companion object: KLogging() {
        private const val REPEAT_SIZE = 3
        private const val CALL_SIZE = 10

    }

    private val jsonApi: JsonPlaceHolderApi by lazy {
        retrofitOf(JsonPlaceHolder.BASE_URL).service()
    }

    @Test
    fun `Retrofit용 API 생성`() {
        jsonApi.shouldNotBeNull()
    }

    @Nested
    inner class Single {

        @Test
        fun `Retrofit용 API를 활용한 동기방식 호출`() {
            val response = jsonApi.getPost(1).execute()
            val post = response.body()
            log.debug { "Post[1]=$post" }
            post.shouldNotBeNull()
        }

        @Test
        fun `Retrofit용 API를 활용한 비동기방식 호출`() {
            val future = jsonApi.getPost(1).executeAsync()

            val response = future.get()
            val post = response.body()
            log.debug { "Post[1]=$post" }
            post.shouldNotBeNull()
        }

        @Test
        fun `Retrofit용 API를 활용한 Coroutines 호출`() = runSuspendWithIO {
            val response = jsonApi.getPost(1).executeAsync().await()

            val post = response.body()
            log.debug { "Post[1]=$post" }
            post.shouldNotBeNull()
        }
    }

    @Nested
    inner class Bulk {

        @RepeatedTest(REPEAT_SIZE)
        fun `Retrofit용 API를 활용한 동기방식 Bulk 호출`() {
            List(CALL_SIZE) {
                jsonApi.getPost(Random.nextInt(1, 100)).execute()
            }
        }

        @RepeatedTest(REPEAT_SIZE)
        fun `Retrofit용 API를 활용한 비동기방식 Bulk 호출`() {
            val future = List(CALL_SIZE) {
                jsonApi.getPost(Random.nextInt(1, 100)).executeAsync()
            }
            val responses = future.allAsList().get()
            responses.forEach { response ->
                response.body().shouldNotBeNull()
            }
        }

        @RepeatedTest(REPEAT_SIZE)
        fun `Retrofit용 API를 활용한 Coroutines Bulk 호출`() = runSuspendWithIO {
            val deferres = List(CALL_SIZE) {
                async {
                    jsonApi.getPost(Random.nextInt(1, 100)).executeAsync().await()
                }
            }

            val responses = deferres.awaitAll()
            responses.forEach { response ->
                response.body().shouldNotBeNull()
            }
        }
    }
}
