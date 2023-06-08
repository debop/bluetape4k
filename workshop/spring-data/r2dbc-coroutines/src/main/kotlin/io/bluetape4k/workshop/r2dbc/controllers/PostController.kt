package io.bluetape4k.workshop.r2dbc.controllers

import io.bluetape4k.workshop.r2dbc.domain.Comment
import io.bluetape4k.workshop.r2dbc.domain.CommentRepository
import io.bluetape4k.workshop.r2dbc.domain.Post
import io.bluetape4k.workshop.r2dbc.domain.PostRepository
import kotlinx.coroutines.flow.Flow
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/posts")
class PostController(
    private val postRepo: PostRepository,
    private val commentRepo: CommentRepository,
) {

    @GetMapping
    fun findAll(): Flow<Post> = postRepo.findAll()

    @GetMapping("/{id}")
    suspend fun findOne(@PathVariable id: Long): Post? =
        postRepo.findOne(id)

    @PostMapping
    suspend fun save(@RequestBody post: Post): Post? {
        return postRepo.save(post)
    }

    @GetMapping("/{postId}/comments")
    fun findCommentsByPostId(@PathVariable postId: Long): Flow<Comment> =
        commentRepo.findByPostId(postId)

    @GetMapping("/{postId}/comments/count")
    suspend fun countCommentsByPostId(@PathVariable postId: Long): Long =
        commentRepo.countByPostId(postId)

    @PostMapping("/{postId}/comments")
    suspend fun saveComment(@PathVariable postId: Long, @RequestBody comment: Comment) {
        commentRepo.save(comment.copy(postId = postId, content = comment.content))
    }
}
