package io.bluetape4k.vertx.sqlclient

import io.vertx.kotlin.coroutines.await
import io.vertx.sqlclient.Row
import io.vertx.sqlclient.RowSet
import io.vertx.sqlclient.SqlClient
import io.vertx.sqlclient.Tuple

suspend fun SqlClient.querySuspending(sql: String): RowSet<Row> {
    return query(sql).execute().await()
}

suspend inline fun <T> SqlClient.querySuspending(
    sql: String,
    mapper: (Row) -> T,
): List<T> {
    return querySuspending(sql).map(mapper)
}

suspend fun SqlClient.querySuspending(sql: String, params: Tuple): RowSet<Row> {
    return preparedQuery(sql).execute(params).await()
}

suspend fun <T> SqlClient.querySuspending(sql: String, params: Tuple, mapper: (Row) -> T): List<T> {
    return querySuspending(sql, params).map(mapper)
}
