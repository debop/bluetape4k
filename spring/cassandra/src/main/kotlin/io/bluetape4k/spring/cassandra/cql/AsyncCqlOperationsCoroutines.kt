package io.bluetape4k.spring.cassandra.cql

import com.datastax.oss.driver.api.core.cql.AsyncResultSet
import com.datastax.oss.driver.api.core.cql.Row
import com.datastax.oss.driver.api.core.cql.Statement
import io.bluetape4k.spring.coroutines.await
import org.springframework.data.cassandra.core.cql.AsyncCqlOperations
import org.springframework.data.cassandra.core.cql.AsyncResultSetExtractor
import org.springframework.util.concurrent.ListenableFuture


suspend inline fun <reified T: Any> AsyncCqlOperations.querySuspending(
    cql: String,
    vararg args: Any,
    crossinline extractor: (AsyncResultSet) -> ListenableFuture<T?>,
): T? =
    query<T>(cql, AsyncResultSetExtractor { extractor(it) }, *args).await()

suspend inline fun <reified T: Any> AsyncCqlOperations.querySuspending(
    cql: String,
    vararg args: Any,
    crossinline rowMapper: (row: Row, rowNum: Int) -> T,
): List<T> =
    query(cql, { row, rowNum -> rowMapper(row, rowNum) }, *args).await()

suspend inline fun <reified T: Any> AsyncCqlOperations.querySuspending(
    statement: Statement<*>,
    crossinline extractor: (AsyncResultSet) -> ListenableFuture<T?>,
): T? =
    query<T>(statement, AsyncResultSetExtractor { extractor(it) }).await()

suspend inline fun <reified T: Any> AsyncCqlOperations.querySuspending(
    statement: Statement<*>,
    crossinline rowMapper: (row: Row, rowNum: Int) -> T,
): List<T> =
    query(statement) { row, rowNum -> rowMapper(row, rowNum) }.await()
