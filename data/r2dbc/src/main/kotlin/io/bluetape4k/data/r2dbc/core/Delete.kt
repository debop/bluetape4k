package io.bluetape4k.data.r2dbc.core

import io.bluetape4k.data.r2dbc.R2dbcClient
import io.bluetape4k.data.r2dbc.query.Query
import io.bluetape4k.data.r2dbc.support.bindMap
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import org.springframework.data.r2dbc.core.ReactiveDeleteOperation
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.FetchSpec
import reactor.core.publisher.Mono

fun R2dbcClient.delete(): DeleteTableSpec = DeleteTableSpecImpl(this)

interface DeleteTableSpec {
    fun from(table: String): DeleteValueSpec
    fun <T> from(type: Class<T>): ReactiveDeleteOperation.ReactiveDelete
}

inline fun <reified T: Any> DeleteTableSpec.from(): ReactiveDeleteOperation.ReactiveDelete = from(T::class.java)

private class DeleteTableSpecImpl(private val client: R2dbcClient): DeleteTableSpec {

    override fun from(table: String): DeleteValueSpec =
        DeleteValueSpecImpl(client, table)

    override fun <T> from(type: Class<T>): ReactiveDeleteOperation.ReactiveDelete =
        client.entityTemplate.delete(type)
}

interface DeleteValueSpec {

    fun matching(
        where: String? = null,
        whereParameters: Map<String, Any?>? = null,
    ): DatabaseClient.GenericExecuteSpec

    fun matching(query: Query): DatabaseClient.GenericExecuteSpec =
        matching(query.sql, query.parameters)

    fun fetch(): FetchSpec<MutableMap<String, Any>> = matching().fetch()
    fun then(): Mono<Void> = matching().then()
}

private class DeleteValueSpecImpl(
    private val client: R2dbcClient,
    private val table: String,
): DeleteValueSpec {

    companion object: KLogging()

    override fun matching(
        where: String?,
        whereParameters: Map<String, Any?>?,
    ): DatabaseClient.GenericExecuteSpec {
        val sql = "DELETE FROM $table"
        val sqlToExecute = when (where) {
            null -> sql
            else -> "$sql WHERE $where"
        }
        log.debug { "Delete SQL=$sqlToExecute" }
        return client.databaseClient.sql(sqlToExecute).bindMap(whereParameters ?: emptyMap())
    }
}
