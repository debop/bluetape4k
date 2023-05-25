package io.bluetape4k.spring.r2dbc

import io.r2dbc.spi.Parameter
import io.r2dbc.spi.Parameters
import org.springframework.r2dbc.core.DatabaseClient

fun DatabaseClient.GenericExecuteSpec.bindMap(parameters: Map<String, Any?>): DatabaseClient.GenericExecuteSpec {
    return parameters.entries.fold(this) { spec, (key, value) ->
        when (value) {
            null         -> spec.bindNull(key, String::class.java)
            is Parameter -> spec.bind(key, value)
            else         -> spec.bind(key, Parameters.`in`(value))
        }
    }
}

fun DatabaseClient.GenericExecuteSpec.bindIndexedMap(parameters: Map<Int, Any?>): DatabaseClient.GenericExecuteSpec {
    return parameters.entries.fold(this) { spec, (key, value) ->
        when (value) {
            null         -> spec.bindNull(key, String::class.java)
            is Parameter -> spec.bind(key, value)
            else         -> spec.bind(key, Parameters.`in`(value))
        }
    }
}

fun DatabaseClient.execute(sql: String, parameters: Map<String, Any?>): DatabaseClient.GenericExecuteSpec {
    return sql(sql).bindMap(parameters)
}
