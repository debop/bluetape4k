package io.bluetape4k.data.r2dbc.core

import io.bluetape4k.data.r2dbc.R2dbcClient
import io.bluetape4k.data.r2dbc.query.Query
import io.bluetape4k.data.r2dbc.support.bindIndexedMap
import io.bluetape4k.data.r2dbc.support.bindMap
import io.bluetape4k.data.r2dbc.support.toParameter
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.r2dbc.spi.Parameters
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.RowsFetchSpec
import kotlin.reflect.KClass


fun R2dbcClient.execute(sql: String) = this.databaseClient.sql(sql)

fun R2dbcClient.execute(sql: String, parameters: Map<String, Any?>): DatabaseClient.GenericExecuteSpec =
    databaseClient.sql(sql).bindMap(parameters)

fun R2dbcClient.execute(query: Query): DatabaseClient.GenericExecuteSpec =
    execute(query.sql, query.parameters)

inline fun <reified T: Any> R2dbcClient.execute(sql: String): BindSpec<T> {
    return BindSpecImpl(this, sql, T::class)
}

interface BindSpec<T: Any> {
    fun bind(index: Int, value: Any): BindSpec<T>
    fun bind(index: Int, value: Any?, type: Class<*>): BindSpec<T>
    fun bindNull(index: Int, type: Class<*>): BindSpec<T>

    fun bind(name: String, value: Any): BindSpec<T>
    fun bind(name: String, value: Any?, type: Class<*>): BindSpec<T>
    fun bindNull(name: String, type: Class<*>): BindSpec<T>

    fun fetch(): RowsFetchSpec<T>
}

inline fun <T: Any, reified V: Any> BindSpec<T>.bindNullable(index: Int, value: V? = null): BindSpec<T> =
    bind(index, value, V::class.java)

inline fun <T: Any, reified V: Any> BindSpec<T>.bindNullable(name: String, value: V? = null): BindSpec<T> =
    bind(name, value, V::class.java)

@PublishedApi
internal class BindSpecImpl<T: Any>(
    private val client: R2dbcClient,
    private val sql: String,
    private val type: KClass<T>,
): BindSpec<T> {

    companion object: KLogging()

    private val indexedParameters = mutableMapOf<Int, Any?>()
    private val namedParameters = mutableMapOf<String, Any?>()

    override fun bind(index: Int, value: Any) = apply {
        indexedParameters[index] = value
    }

    override fun bind(index: Int, value: Any?, type: Class<*>) = apply {
        indexedParameters[index] = value.toParameter(type)
    }

    override fun bind(name: String, value: Any): BindSpec<T> = apply {
        namedParameters[name] = value
    }

    override fun bind(name: String, value: Any?, type: Class<*>) = apply {
        namedParameters[name] = value.toParameter(type)
    }

    override fun bindNull(index: Int, type: Class<*>) = apply {
        indexedParameters[index] = Parameters.`in`(type)
    }

    override fun bindNull(name: String, type: Class<*>) = apply {
        namedParameters[name] = Parameters.`in`(type)
    }

    override fun fetch(): RowsFetchSpec<T> {
        log.debug { "sql=$sql, named params=$namedParameters" }
        return client.databaseClient
            .sql(sql)
            .bindMap(namedParameters)
            .bindIndexedMap(indexedParameters)
            .map { row, rowMetadata ->
                client.mappingConverter.read(type.java, row, rowMetadata)
            }
    }
}

inline fun <reified T: Any> R2dbcClient.execute(
    sql: String,
    parameters: Map<String, Any?> = emptyMap(),
): RowsFetchSpec<T> {
    return databaseClient
        .sql(sql)
        .bindMap(parameters)
        .map { row, rowMetadata ->
            mappingConverter.read(T::class.java, row, rowMetadata)
        }
}

inline fun <reified T: Any> R2dbcClient.execute(query: Query): RowsFetchSpec<T> {
    return execute<T>(query.sql, query.parameters)
}
