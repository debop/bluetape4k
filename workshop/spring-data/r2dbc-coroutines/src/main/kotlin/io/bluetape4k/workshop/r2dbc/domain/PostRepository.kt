package io.bluetape4k.workshop.r2dbc.domain

import io.bluetape4k.logging.KLogging
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.r2dbc.convert.MappingR2dbcConverter
import org.springframework.data.r2dbc.core.R2dbcEntityOperations
import org.springframework.data.r2dbc.core.allAndAwait
import org.springframework.data.r2dbc.core.asType
import org.springframework.data.r2dbc.core.awaitCount
import org.springframework.data.r2dbc.core.delete
import org.springframework.data.r2dbc.core.flow
import org.springframework.data.r2dbc.core.insert
import org.springframework.data.r2dbc.core.select
import org.springframework.data.relational.core.query.Criteria
import org.springframework.data.relational.core.query.Query
import org.springframework.data.relational.core.query.isEqual
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Repository

@Repository
class PostRepository(
    private val client: DatabaseClient,
    private val operations: R2dbcEntityOperations,
    private val mappingR2dbcConverter: MappingR2dbcConverter,
) {

    companion object: KLogging()

    suspend fun count(): Long {
        return operations.select<Post>().awaitCount()
        // return operations.count(Query.empty(), Post::class.java).awaitSingle()

//        return client.sql("SELECT COUNT(*) FROM Posts")
//            .map { row, rowMetadata ->
//                mappingR2dbcConverter.read<Long>(row, rowMetadata)
//            }
//            .one()
//            .awaitSingle()
    }

    fun findAll(): Flow<Post> {
        return operations.select<Post>()
            .from("posts")
            .asType<Post>()
            .flow()
    }

    suspend fun findOne(id: Long): Post? {
        return operations
            .selectOne(Query.query(Criteria.where(Post::id.name).isEqual(id)), Post::class.java)
            .awaitSingleOrNull()
    }

    suspend fun findById(id: Long): Post? {
        return operations
            .select(Query.query(Criteria.where(Post::id.name).isEqual(id)), Post::class.java)
            .awaitFirstOrNull()
    }

    suspend fun deleteAll(): Int {
        return operations.delete<Post>().allAndAwait()
    }

    suspend fun save(post: Post): Post? {
        return operations.insert<Post>()
            .into("posts")
            .using(post)
            .awaitSingleOrNull()
    }

    suspend fun init() {
        save(Post(title = "My first post title", content = "Content of my first post"))
        save(Post(title = "My second post title", content = "Content of my second post"))
    }
}
