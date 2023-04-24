package io.bluetape4k.io.retrofit2.client

import io.bluetape4k.io.retrofit2.defaultJsonConverterFactory
import io.bluetape4k.io.retrofit2.retrofitOf
import io.bluetape4k.io.retrofit2.service
import io.bluetape4k.io.retrofit2.services.JsonPlaceHolder
import io.bluetape4k.io.retrofit2.services.Post
import io.bluetape4k.junit5.coroutines.runSuspendWithIO
import io.bluetape4k.junit5.random.RandomValue
import io.bluetape4k.junit5.random.RandomizedTest
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeNull
import org.amshove.kluent.shouldBeTrue
import org.amshove.kluent.shouldContainSame
import org.amshove.kluent.shouldNotBeEmpty
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.Test
import kotlin.math.absoluteValue
import kotlin.random.Random

@RandomizedTest
abstract class AbstractJsonPlaceHolderCoroutineTest: AbstractJsonPlaceHolderTest() {

    companion object: KLogging() {
        private const val ITEM_SIZE = 5
    }

    private val api: JsonPlaceHolder.JsonPlaceHolderCoroutineApi by lazy {
        retrofitOf(JsonPlaceHolder.BASE_URL, callFactory, defaultJsonConverterFactory).service()
    }

    @Test
    fun `create retrofit2 api instance`() {
        api.shouldNotBeNull()
    }

    @Test
    fun `get posts`() = runSuspendWithIO {
        val posts = api.posts()

        posts.shouldNotBeEmpty()
        posts.forEach { it.verify() }
    }

    @Test
    fun `get post by postId`() = runSuspendWithIO {
        val postIds = List(ITEM_SIZE) { Random.nextInt(1, 100) }.distinct()

        val deferred = postIds.map { postId ->
            async(Dispatchers.IO) { api.getPost(postId) }
        }

        val posts = deferred.awaitAll()
        posts.shouldNotBeEmpty()
        posts.forEach { it.verify() }
        posts.map { it.id } shouldContainSame postIds
    }

    @Test
    fun `get user's posts`() = runSuspendWithIO {
        val userIds = List(ITEM_SIZE) { Random.nextInt(1, 100) }.distinct()

        val deferred = userIds.map { userId ->
            async {
                userId to api.getUserPosts(userId)
            }
        }
        val userPosts = deferred.awaitAll().toMap()

        userPosts.size shouldBeEqualTo userIds.size
        userPosts.keys shouldContainSame userIds
        userPosts.forEach { (userId, posts) ->
            userIds.contains(userId).shouldBeTrue()
            posts.forEach { it.verify() }
        }
    }

    @Test
    fun `get post's comments`() = runSuspendWithIO {
        val postIds = List(ITEM_SIZE) { Random.nextInt(1, 100) }.distinct()

        val deferred = postIds.map { postId ->
            async {
                postId to api.getPostComments(postId)
            }
        }

        val postComments = deferred.awaitAll().toMap()

        postComments.size shouldBeEqualTo postIds.size
        postComments.keys shouldContainSame postIds
        postComments.forEach { (postId, comments) ->
            postIds.contains(postId).shouldBeTrue()
            comments.forEach { it.verify() }
        }
    }

    @Test
    fun `get all users`() = runSuspendWithIO {
        val users = api.getUsers()
        users.shouldNotBeEmpty()
    }

    @Test
    fun `get albums by userId`() = runSuspendWithIO {
        val userIds = List(ITEM_SIZE) { Random.nextInt(1, 100) }.distinct()

        val deferred = userIds.map { userId ->
            async {
                userId to api.getAlbumsByUserId(userId)
            }
        }

        val userAlbums = deferred.awaitAll().toMap()

        userAlbums.size shouldBeEqualTo userIds.size
        userAlbums.keys shouldContainSame userIds
        userAlbums.forEach { (userId, albums) ->
            userIds.contains(userId).shouldBeTrue()
            albums.forEach { it.verify() }
        }
    }

    @Test
    fun `create new post`(@RandomValue(type = Post::class, size = ITEM_SIZE) posts: List<Post>) = runSuspendWithIO {
        val deferred = posts.map { post ->
            async {
                api.newPost(post.copy(userId = post.userId.absoluteValue))
            }
        }

        val newPosts = deferred.awaitAll()
        newPosts.forEach { newPost -> newPost.verify() }
    }

    @Test
    fun `update exists post`() = runSuspendWithIO {
        val postIds = List(ITEM_SIZE) { Random.nextInt(1, 100) }.distinct()

        val deferred = postIds.map { postId ->
            async {
                val post = api.getPost(postId)
                api.updatePost(postId, post.copy(title = "Updated " + post.title))
            }
        }

        val updated = deferred.awaitAll()
        updated.size shouldBeEqualTo postIds.size
        updated.forEach { it.verify() }
    }

    @Test
    fun `delete post`(@RandomValue post: Post) = runSuspendWithIO {
        val newPost = post.copy(userId = post.userId.absoluteValue)
        val saved = api.newPost(newPost)
        val savedPostId = saved.id
        log.debug { "saved=$saved" }

        val deleted = api.deletePost(savedPostId)
        log.debug { "deleted=$deleted" }
        deleted.id shouldBeEqualTo 0
        deleted.userId shouldBeEqualTo 0
        deleted.title.shouldBeNull()
        deleted.body.shouldBeNull()
    }
}
