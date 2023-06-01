package io.bluetape4k.data.r2dbc.core

import io.bluetape4k.data.r2dbc.R2dbcClient
import io.bluetape4k.data.r2dbc.query.Query
import org.springframework.data.r2dbc.core.ReactiveUpdateOperation
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.FetchSpec
import reactor.core.publisher.Mono
import kotlin.reflect.KProperty

fun R2dbcClient.update(): UpdateTableSpec {
    return UpdateTableSpecImpl(this)
}

interface UpdateTableSpec {
    fun table(table: String): UpdateValuesSpec
    fun <T> table(domainType: Class<T>): ReactiveUpdateOperation.ReactiveUpdate
}

inline fun <reified T: Any> UpdateTableSpec.table(): ReactiveUpdateOperation.ReactiveUpdate {
    return table(T::class.java)
}

internal class UpdateTableSpecImpl(private val client: R2dbcClient): UpdateTableSpec {
    override fun table(table: String): UpdateValuesSpec {
        return UpdateValuesSpecImpl(client, table)
    }

    override fun <T> table(domainType: Class<T>): ReactiveUpdateOperation.ReactiveUpdate {
        return client.entityTemplate.update(domainType)
    }
}

interface SetterSpec {
    val Update: SetterSpec

    fun update(field: String, value: Any): UpdateValuesSpec
    fun update(field: String, value: Any?, type: Class<*>): UpdateValuesSpec
    fun set(field: String, value: Any): UpdateValuesSpec
    fun set(field: String, value: Any?, type: Class<*>): UpdateValuesSpec
    fun set(parameters: Map<String, Any?>): UpdateValuesSpec
    val values: Map<String, Any?>

    fun update(property: KProperty<*>, value: Any) = update(property.name, value)
    fun update(property: KProperty<*>, value: Any?, type: Class<*>) = update(property.name, value, type)
    fun set(property: KProperty<*>, value: Any) = set(property.name, value)
    fun set(property: KProperty<*>, value: Any?, type: Class<*>) = set(property.name, value, type)
}

inline fun <reified T: Any> SetterSpec.updateNullable(field: String, value: T? = null) =
    update(field, value, T::class.java)

inline fun <reified T: Any> SetterSpec.updateNullable(property: KProperty<*>, value: T? = null) =
    update(property, value, T::class.java)

inline fun <reified T: Any> SetterSpec.setNullable(field: String, value: T? = null) =
    set(field, value, T::class.java)

inline fun <reified T: Any> SetterSpec.setNullable(property: KProperty<*>, value: T? = null) =
    set(property, value, T::class.java)

interface UpdateValuesSpec: SetterSpec {
    fun using(setters: SetterSpec.() -> Unit): UpdateValuesSpec
    fun matching(where: String? = null, whereParameters: Map<String, Any?>? = null): DatabaseClient.GenericExecuteSpec
    fun matching(query: Query): DatabaseClient.GenericExecuteSpec =
        matching(query.sql, query.parameters)

    fun fetch(): FetchSpec<MutableMap<String, Any>> {
        return matching().fetch()
    }

    fun then(): Mono<Void> {
        return matching().then()
    }
}

internal class UpdateValuesSpecImpl(
    private val client: R2dbcClient,
    private val table: String,
): UpdateValuesSpec {

    override val values = mutableMapOf<String, Any?>()
    override val Update: SetterSpec get() = this

    override fun using(setters: SetterSpec.() -> Unit): UpdateValuesSpec {
        TODO("Not yet implemented")
    }

    override fun matching(where: String?, whereParameters: Map<String, Any?>?): DatabaseClient.GenericExecuteSpec {
        TODO("Not yet implemented")
    }

    override fun update(field: String, value: Any): UpdateValuesSpec {
        TODO("Not yet implemented")
    }

    override fun update(field: String, value: Any?, type: Class<*>): UpdateValuesSpec {
        TODO("Not yet implemented")
    }

    override fun set(field: String, value: Any): UpdateValuesSpec {
        TODO("Not yet implemented")
    }

    override fun set(field: String, value: Any?, type: Class<*>): UpdateValuesSpec {
        TODO("Not yet implemented")
    }

    override fun set(parameters: Map<String, Any?>): UpdateValuesSpec {
        TODO("Not yet implemented")
    }


}
