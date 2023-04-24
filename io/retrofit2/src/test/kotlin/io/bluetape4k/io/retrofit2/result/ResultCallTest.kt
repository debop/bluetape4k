package io.bluetape4k.io.retrofit2.result

import io.bluetape4k.io.retrofit2.AbstractRetrofitTest
import io.bluetape4k.io.retrofit2.clients.vertx.vertxCallFactoryOf
import io.bluetape4k.io.retrofit2.defaultJsonConverterFactory
import io.bluetape4k.io.retrofit2.retrofitBuilderOf
import io.bluetape4k.io.retrofit2.service
import io.bluetape4k.io.retrofit2.services.JsonPlaceHolder
import io.bluetape4k.io.retrofit2.services.Post
import io.bluetape4k.logging.KLogging
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldBeTrue
import org.amshove.kluent.shouldNotBeEmpty
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.Test
import retrofit2.HttpException
import retrofit2.http.GET
import retrofit2.http.Path

class ResultCallTest : AbstractRetrofitTest() {

    companion object : KLogging()

    /**
     * [Json Place Holder](https://jsonplaceholder.typicode.com/) 에서 제공하는 API 로서 Json 데이터 통신에 대한 테스트를 손쉽게 할 수 있습니다.
     *
     * 여기서는 API 통신을 Coroutines 를 이용합니다.
     */
    interface JsonPlaceHolderCoroutineResultApi {

        @GET("/posts")
        suspend fun posts(): Result<List<Post>>

        @GET("/posts/{id}")
        suspend fun getPost(@Path("id") postId: Int): Result<Post>

    }

    private val retrofit = retrofitBuilderOf(JsonPlaceHolder.BASE_URL)
        .callFactory(vertxCallFactoryOf())
        .addConverterFactory(defaultJsonConverterFactory)
        .addCallAdapterFactory(ResultCallAdapterFactory())
        .build()

    private val api by lazy { retrofit.service<JsonPlaceHolderCoroutineResultApi>() }

    @Test
    fun `get posts with result`() = runTest {
        api.posts().isSuccess.shouldBeTrue()
    }

    @Test
    fun `get exist post with result`() = runTest {
        val posts = api.posts().getOrThrow()
        posts.shouldNotBeEmpty()

        val firstPost = api.getPost(posts.first().id)
        firstPost.isSuccess.shouldBeTrue()
    }

    @Test
    fun `get no-exists post with result`() = runTest {
        val posts = api.posts().getOrThrow()
        posts.shouldNotBeEmpty()

        val notExists = api.getPost(-1)
        notExists.isFailure.shouldBeTrue()
        notExists.exceptionOrNull().shouldNotBeNull() shouldBeInstanceOf HttpException::class
    }
}
