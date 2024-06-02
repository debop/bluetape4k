package io.bluetape4k.workshop.r2dbc.domain

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.workshop.r2dbc.AbstractApplicationTest
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeNull
import org.amshove.kluent.shouldNotBeEmpty
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class PostRepositoryTest(
    @Autowired private val postRepo: PostRepository,
): AbstractApplicationTest() {

    companion object: KLogging()

    @Test
    fun `context loading`() {
        postRepo.shouldNotBeNull()
    }

    @Test
    fun `find all posts`() = runTest {
        val posts = postRepo.findAll().toList()
        posts.forEach { post -> log.debug { post } }
        posts.shouldNotBeEmpty()
    }

    @Test
    fun `find one post by id`() = runTest {
        val post = postRepo.findOne(1)
        post.shouldNotBeNull()
        post.id shouldBeEqualTo 1
    }

    @Test
    fun `find one post by id - not exists`() = runTest {
        postRepo.findOne(9999).shouldBeNull()
    }

    @Test
    fun `find post by id`() = runTest {
        val post = postRepo.findById(1)
        post.shouldNotBeNull()
        post.id shouldBeEqualTo 1
    }

    @Test
    fun `find post by id - not exists`() = runTest {
        postRepo.findById(9999).shouldBeNull()
    }

    @Test
    fun `save new post`() = runTest {
        val oldCount = postRepo.count()

        val newPost = createPost()
        val savedPost = postRepo.save(newPost)
        savedPost.shouldNotBeNull()
        savedPost.id.shouldNotBeNull()

        val newCount = postRepo.count()

        newCount shouldBeEqualTo oldCount + 1
    }
}
