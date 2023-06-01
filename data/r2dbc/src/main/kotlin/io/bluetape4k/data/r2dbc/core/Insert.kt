package io.bluetape4k.data.r2dbc.core

import io.bluetape4k.data.r2dbc.R2dbcClient
import org.springframework.data.r2dbc.core.ReactiveInsertOperation
import org.springframework.r2dbc.core.FetchSpec
import org.springframework.r2dbc.core.RowsFetchSpec
import org.springframework.r2dbc.core.awaitOne
import reactor.core.publisher.Mono
import kotlin.reflect.KProperty

fun R2dbcClient.insert(): InsertIntoSpec {
    return InsertIntoSpecImpl(this)
}

interface InsertIntoSpec {
    fun into(table: String): InsertValuesSpec
    fun into(table: String, idColumn: String): InsertValuesKeySpec
    fun into(table: String, idProperty: KProperty<*>): InsertValuesKeySpec = into(table, idProperty.name)
    fun <T> into(type: Class<T>): ReactiveInsertOperation.ReactiveInsert<T>
}

internal class InsertIntoSpecImpl(private val client: R2dbcClient): InsertIntoSpec {
    override fun into(table: String): InsertValuesSpec {
        return InsertValuesSpecImpl(client, table)
    }

    override fun into(table: String, idColumn: String): InsertValuesKeySpec {
        return InsertValuesKeySpecImpl(client, table, idColumn)
    }

    override fun <T> into(type: Class<T>): ReactiveInsertOperation.ReactiveInsert<T> {
        return client.entityTemplate.insert(type)
    }
}

interface InsertValuesSpec {
    fun value(field: String, value: Any): InsertValuesSpec
    fun value(field: String, value: Any?, type: Class<*>): InsertValuesSpec

    fun value(property: KProperty<*>, value: Any): InsertValuesSpec = value(property.name, value)
    fun value(property: KProperty<*>, value: Any?, type: Class<*>): InsertValuesSpec = value(property.name, value, type)

    fun nullValue(field: String): InsertValuesSpec
    fun nullValue(field: String, type: Class<*>): InsertValuesSpec
    fun nullValue(property: KProperty<*>): InsertValuesSpec = nullValue(property.name)
    fun nullValue(property: KProperty<*>, type: Class<*>): InsertValuesSpec = nullValue(property.name, type)

    fun fetch(): FetchSpec<out Any>
    fun then(): Mono<Void>
    suspend fun await()
    val values: Map<String, Any?>
}

inline fun <reified T: Any> InsertValuesSpec.valueNullable(field: String, value: T? = null) =
    value(field, value, T::class.java)

inline fun <reified T: Any> InsertValuesSpec.valueNullable(property: KProperty<*>, value: T? = null) =
    value(property, value, T::class.java)

internal class InsertValuesSpecImpl(
    private val client: R2dbcClient,
    private val table: String,
): InsertValuesSpec {

    override fun value(field: String, value: Any): InsertValuesSpec {
        TODO("Not yet implemented")
    }

    override fun value(field: String, value: Any?, type: Class<*>): InsertValuesSpec {
        TODO("Not yet implemented")
    }

    override fun nullValue(field: String): InsertValuesSpec {
        TODO("Not yet implemented")
    }

    override fun nullValue(field: String, type: Class<*>): InsertValuesSpec {
        TODO("Not yet implemented")
    }

    override fun fetch(): FetchSpec<out Any> {
        TODO("Not yet implemented")
    }

    override fun then(): Mono<Void> {
        TODO("Not yet implemented")
    }

    override suspend fun await() {
        TODO("Not yet implemented")
    }

    override val values: Map<String, Any?>
        get() = TODO("Not yet implemented")
}

interface InsertValuesKeySpec {
    fun value(field: String, value: Any): InsertValuesKeySpec
    fun value(field: String, value: Any?, type: Class<*>): InsertValuesKeySpec

    fun value(property: KProperty<*>, value: Any) = value(property.name, value)
    fun value(property: KProperty<*>, value: Any?, type: Class<*>) = value(property.name, value, type)

    fun nullValue(field: String): InsertValuesKeySpec
    fun nullValue(field: String, type: Class<*>): InsertValuesKeySpec
    fun nullValue(property: KProperty<*>) = nullValue(property.name)
    fun nullValue(property: KProperty<*>, type: Class<*>) = nullValue(property.name, type)

    fun fetch(): RowsFetchSpec<Int>
    suspend fun awaitOne(): Int

    fun fetchLong(): RowsFetchSpec<Long>
    suspend fun awaitOneLong(): Long

    fun then(): Mono<Void>
    val values: Map<String, Any?>
}

inline fun <reified T: Any> InsertValuesKeySpec.valueNullable(field: String, value: T? = null) =
    value(field, value, T::class.java)

inline fun <reified T: Any> InsertValuesKeySpec.valueNullable(property: KProperty<*>, value: T? = null) =
    value(property, value, T::class.java)

internal class InsertValuesKeySpecImpl(
    private val client: R2dbcClient,
    private val table: String,
    private val idColumn: String,
): InsertValuesKeySpec {

    override val values: Map<String, Any?> = mutableMapOf()

    override fun value(field: String, value: Any): InsertValuesKeySpec {
        TODO("Not yet implemented")
    }

    override fun value(field: String, value: Any?, type: Class<*>): InsertValuesKeySpec {
        TODO("Not yet implemented")
    }

    override fun nullValue(field: String): InsertValuesKeySpec {
        TODO("Not yet implemented")
    }

    override fun nullValue(field: String, type: Class<*>): InsertValuesKeySpec {
        TODO("Not yet implemented")
    }

    override fun fetch(): RowsFetchSpec<Int> {
        TODO("Not yet implemented")
    }

    override suspend fun awaitOne(): Int {
        return fetch().awaitOne()
    }

    override fun fetchLong(): RowsFetchSpec<Long> {
        TODO("Not yet implemented")
    }

    override suspend fun awaitOneLong(): Long {
        return fetchLong().awaitOne()
    }

    override fun then(): Mono<Void> {
        return then()
    }
}
