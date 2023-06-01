package io.bluetape4k.workshop.r2dbc.domain

import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Component

@Component
class CommentRepository(private val client: DatabaseClient) {

    suspend fun save(comment: Comment) {

    }
}
