package io.bluetape4k.workshop.r2dbc.controllers

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.workshop.r2dbc.AbstractApplicationTest
import io.bluetape4k.workshop.r2dbc.domain.Comment
import io.bluetape4k.workshop.r2dbc.domain.Post
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeGreaterThan
import org.amshove.kluent.shouldBeNull
import org.amshove.kluent.shouldBeTrue
import org.amshove.kluent.shouldNotBeEmpty
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import org.springframework.test.web.reactive.server.expectBodyList

class PostControllerTest(
    @Autowired private val client: WebTestClient,
): AbstractApplicationTest() {

    companion object: KLogging()

    @Test
    fun `find all posts`() = runTest {
        val posts = client.get()
            .uri("/posts")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBodyList<Post>()
            .returnResult()
            .responseBody.shouldNotBeNull()

        posts.shouldNotBeEmpty()
        posts.forEach { post ->
            log.debug { post }
        }
    }

    @Test
    fun `find one post by id`() = runTest {
        val post = client.get()
            .uri("/posts/1")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody<Post>()
            .returnResult()
            .responseBody.shouldNotBeNull()

        log.debug { "Post[1]=$post" }
        post.id shouldBeEqualTo 1
    }

    @Test
    fun `find one post by non-existing id`() = runTest {
        client.get()
            .uri("/posts/9999")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody<Post>()
            .returnResult()
            .responseBody.shouldBeNull()
    }

    @Test
    fun `save new post`() = runTest {
        val newPost = createPost()

        val savedPost = client.post()
            .uri("/posts")
            .bodyValue(newPost)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody<Post>()
            .returnResult()
            .responseBody.shouldNotBeNull()

        savedPost.id.shouldNotBeNull()
        savedPost shouldBeEqualTo newPost.copy(id = savedPost.id)
    }

    @Test
    fun `find all comments by post id`() = runTest {
        val comments = client.get()
            .uri("/posts/1/comments")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBodyList<Comment>()
            .returnResult()
            .responseBody.shouldNotBeNull()

        comments.forEach {
            log.debug { it }
        }
        comments.shouldNotBeEmpty()
        comments.all { it.postId == 1L }.shouldBeTrue()
    }

    @Test
    fun `count of comments by post id`() = runTest {
        val commentCount1 = countOfCommentByPostId(1)
        val commentCount2 = countOfCommentByPostId(2)

        commentCount1 shouldBeGreaterThan 0
        commentCount2 shouldBeGreaterThan 0
    }

    @Test
    fun `count of comments by non-existing post id`() = runTest {
        countOfCommentByPostId(9999) shouldBeEqualTo 0L
    }

    private suspend fun countOfCommentByPostId(postId: Long): Long {
        return client.get()
            .uri("/posts/$postId/comments/count")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody<Long>()
            .returnResult()
            .responseBody.shouldNotBeNull()
    }
}
