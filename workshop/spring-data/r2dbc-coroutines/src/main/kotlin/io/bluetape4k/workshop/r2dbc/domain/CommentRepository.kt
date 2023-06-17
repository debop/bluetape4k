package io.bluetape4k.workshop.r2dbc.domain

import io.bluetape4k.logging.KLogging
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.data.r2dbc.core.R2dbcEntityOperations
import org.springframework.data.r2dbc.core.flow
import org.springframework.data.r2dbc.core.insert
import org.springframework.data.r2dbc.core.select
import org.springframework.data.relational.core.query.Criteria
import org.springframework.data.relational.core.query.Query
import org.springframework.data.relational.core.query.isEqual
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Repository

@Repository
class CommentRepository(
    private val client: DatabaseClient,
    private val operations: R2dbcEntityOperations,
) {

    companion object: KLogging()

    suspend fun save(comment: Comment): Comment {
        return operations.insert<Comment>().using(comment).awaitSingle()
    }

    suspend fun countByPostId(postId: Long): Long {
        val query = Query.query(Criteria.where("post_id").isEqual(postId))
        return operations.count(query, Comment::class.java).awaitSingle()
    }

    fun findByPostId(postId: Long): Flow<Comment> {
        val query = Query.query(Criteria.where("post_id").isEqual(postId))
        return operations.select<Comment>().matching(query).flow()
    }

    suspend fun init() {
        save(Comment(postId = 1, content = "Content 1 of post 1"))
        save(Comment(postId = 1, content = "Content 2 of post 1"))
        save(Comment(postId = 2, content = "Content 1 of post 2"))
        save(Comment(postId = 2, content = "Content 2 of post 2"))
    }

}
