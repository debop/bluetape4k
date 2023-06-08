package io.bluetape4k.data.r2dbc.core

import org.springframework.r2dbc.core.DatabaseClient

inline fun databaseClient(
    initializer: DatabaseClient.Builder.() -> Unit,
): DatabaseClient {
    return DatabaseClient.builder().also(initializer).build()
}
