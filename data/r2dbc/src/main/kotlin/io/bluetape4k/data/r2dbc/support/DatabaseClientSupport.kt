package io.bluetape4k.data.r2dbc.support

import org.springframework.r2dbc.core.DatabaseClient

fun DatabaseClient.GenericExecuteSpec.bindMap(parameters: Map<String, Any?>) = apply {
    parameters.entries.fold(this) { spec, entry ->
        val value = entry.value
        when (value) {
            null -> spec.bindNull(entry.key, String::class.java)
            else -> spec.bind(entry.key, value.toParameter())
        }
    }
}

fun DatabaseClient.GenericExecuteSpec.bindIndexedMap(parameters: Map<Int, Any?>) = apply {
    parameters.entries.fold(this) { spec, entry ->
        val value = entry.value
        when (value) {
            null -> spec.bindNull(entry.key, String::class.java)
            else -> spec.bind(entry.key, value.toParameter())
        }
    }
}

fun DatabaseClient.execute(sql: String): DatabaseClient.GenericExecuteSpec {
    return sql(sql)
}

fun DatabaseClient.execute(sql: String, parameters: Map<String, Any?>): DatabaseClient.GenericExecuteSpec {
    return sql(sql).bindMap(parameters)
}

inline fun <reified V: Any> DatabaseClient.GenericExecuteSpec.bindNullable(
    index: Int,
    value: V? = null,
) = apply {
    bind(index, value.toParameter(V::class.java))
}

inline fun <reified V: Any> DatabaseClient.GenericExecuteSpec.bindNullable(
    name: String,
    value: V? = null,
) = apply {
    bind(name, value.toParameter(V::class.java))
}
