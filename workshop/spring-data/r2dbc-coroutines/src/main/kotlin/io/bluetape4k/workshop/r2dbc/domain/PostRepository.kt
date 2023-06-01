package io.bluetape4k.workshop.r2dbc.domain

import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.data.r2dbc.convert.MappingR2dbcConverter
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Component

@Component
class PostRepository(
    private val client: DatabaseClient,
    private val mappingR2dbcConverter: MappingR2dbcConverter,
) {

    suspend fun count(): Long {
        return client.sql("SELECT COUNT(*) FROM Posts")
            .map { row, rowMetadata ->
                mappingR2dbcConverter.read(Long::class.java, row, rowMetadata)
            }
            .one()
            .awaitSingle()
    }
}
