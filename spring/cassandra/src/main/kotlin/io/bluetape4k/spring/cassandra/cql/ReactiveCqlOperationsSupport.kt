package io.bluetape4k.spring.cassandra.cql

import com.datastax.oss.driver.api.core.cql.PreparedStatement
import com.datastax.oss.driver.api.core.cql.Row
import com.datastax.oss.driver.api.core.cql.Statement
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.asPublisher
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.cassandra.ReactiveResultSet
import org.springframework.data.cassandra.ReactiveSession
import org.springframework.data.cassandra.core.cql.PreparedStatementBinder
import org.springframework.data.cassandra.core.cql.ReactiveCqlOperations
import org.springframework.data.cassandra.core.cql.ReactivePreparedStatementCreator
import org.springframework.data.cassandra.core.cql.ReactiveSessionCallback

fun <T: Any> ReactiveCqlOperations.executeSuspending(action: (ReactiveSession) -> Flow<T>): Flow<T> =
    execute(ReactiveSessionCallback { session -> action(session).asPublisher() }).asFlow()

suspend fun ReactiveCqlOperations.executeSuspending(cql: String): Boolean? =
    execute(cql).awaitSingleOrNull()

suspend fun ReactiveCqlOperations.executeSuspending(psc: ReactivePreparedStatementCreator): Boolean? =
    execute(psc).awaitSingleOrNull()

fun ReactiveCqlOperations.executeSuspending(cql: String, args: () -> Flow<Array<Any?>>): Flow<Boolean?> =
    execute(cql, args().asPublisher()).asFlow()

suspend fun <T: Any> ReactiveCqlOperations.queryForObjectSuspending(
    cql: String,
    vararg args: Any?,
    rowMapper: (Row, Int) -> T?,
): T? {
    return queryForObject(cql, rowMapper, *args).awaitSingleOrNull()
}

suspend inline fun <reified T: Any> ReactiveCqlOperations.queryForObjectSuspending(cql: String, vararg args: Any): T? {
    return queryForObject(cql, T::class.java, *args).awaitSingleOrNull()
}

suspend inline fun <reified T: Any> ReactiveCqlOperations.queryForObjectSuspending(statement: Statement<*>): T? {
    return queryForObject(statement, T::class.java).awaitSingleOrNull()
}


suspend fun ReactiveCqlOperations.queryForMapSuspending(cql: String, vararg args: Any): Map<String, Any?> =
    queryForMap(cql, args).awaitSingle()

inline fun <reified T: Any> ReactiveCqlOperations.queryForFlow(cql: String, vararg args: Any): Flow<T> =
    queryForFlux(cql, T::class.java, *args).asFlow()

fun ReactiveCqlOperations.queryForMapFlow(cql: String, vararg args: Any): Flow<Map<String, Any?>> =
    queryForFlux(cql, *args).asFlow()

suspend fun ReactiveCqlOperations.queryForResultSetSuspending(cql: String, vararg args: Any): ReactiveResultSet =
    queryForResultSet(cql, *args).awaitSingle()

fun ReactiveCqlOperations.queryForRowsFlow(statement: Statement<*>): Flow<Row> =
    queryForRows(statement).asFlow()

fun ReactiveCqlOperations.queryForRowsFlow(cql: String, vararg args: Any): Flow<Row> =
    queryForRows(cql, *args).asFlow()


fun ReactiveCqlOperations.executeForFlow(statementFlow: Flow<String>): Flow<Boolean> =
    execute(statementFlow.asPublisher()).asFlow()

suspend fun ReactiveCqlOperations.executeSuspending(statement: Statement<*>): Boolean =
    execute(statement).awaitSingle()

fun <T: Any> ReactiveCqlOperations.queryForFlow(statement: Statement<*>, rse: (ReactiveResultSet) -> Flow<T>): Flow<T> =
    query(statement) { rs -> rse(rs).asPublisher() }.asFlow()

fun <T: Any> ReactiveCqlOperations.queryForFlow(statement: Statement<*>, rowMapper: (Row, Int) -> T): Flow<T> =
    query(statement) { row, rowNum -> rowMapper(row, rowNum) }.asFlow()

suspend fun ReactiveCqlOperations.queryForMapSuspending(statement: Statement<*>): Map<String, Any?> =
    queryForMap(statement).awaitSingle()

inline fun <reified T: Any> ReactiveCqlOperations.queryForFlow(statement: Statement<*>): Flow<T> =
    queryForFlux(statement, T::class.java).asFlow()

fun ReactiveCqlOperations.queryForMapFlow(statement: Statement<*>): Flow<Map<String, Any?>> =
    queryForFlux(statement).asFlow()

suspend fun ReactiveCqlOperations.queryForResultSetSuspending(statement: Statement<*>): ReactiveResultSet =
    queryForResultSet(statement).awaitSingle()

fun <T: Any> ReactiveCqlOperations.executeForFlow(
    psc: ReactivePreparedStatementCreator,
    action: (ReactiveSession, PreparedStatement) -> Flow<T>,
): Flow<T> =
    execute(psc) { rs, ps -> action(rs, ps).asPublisher() }.asFlow()

fun <T: Any> ReactiveCqlOperations.executeForFlow(
    cql: String,
    action: (ReactiveSession, PreparedStatement) -> Flow<T>,
): Flow<T> =
    execute(cql) { rs, ps -> action(rs, ps).asPublisher() }.asFlow()

fun <T: Any> ReactiveCqlOperations.queryForFlow(
    cql: String,
    vararg args: Any?,
    rse: (ReactiveResultSet) -> Flow<T>,
): Flow<T> =
    query(cql, { rs -> rse(rs).asPublisher() }, *args).asFlow()

fun <T: Any> ReactiveCqlOperations.queryForFlow(
    cql: String,
    vararg args: Any,
    rowMapper: (Row, Int) -> T,
): Flow<T> =
    query(cql, rowMapper, *args).asFlow()

fun <T: Any> ReactiveCqlOperations.queryForFlow(
    psc: ReactivePreparedStatementCreator,
    rse: (ReactiveResultSet) -> Flow<T>,
): Flow<T> =
    query(psc) { rs -> rse(rs).asPublisher() }.asFlow()

fun <T: Any> ReactiveCqlOperations.queryForFlow(
    cql: String,
    psb: PreparedStatementBinder? = null,
    rse: (ReactiveResultSet) -> Flow<T>,
): Flow<T> =
    query(cql, psb) { rs -> rse(rs).asPublisher() }.asFlow()

fun <T: Any> ReactiveCqlOperations.queryForFlow(
    psc: ReactivePreparedStatementCreator,
    psb: PreparedStatementBinder? = null,
    rse: (ReactiveResultSet) -> Flow<T>,
): Flow<T> =
    query(psc, psb) { rs -> rse(rs).asPublisher() }.asFlow()

fun <T: Any> ReactiveCqlOperations.queryForFlow(
    psc: ReactivePreparedStatementCreator,
    rowMapper: (row: Row, rowNum: Int) -> T,
): Flow<T> =
    query(psc, rowMapper).asFlow()


fun <T: Any> ReactiveCqlOperations.queryForFlow(
    psc: ReactivePreparedStatementCreator,
    psb: PreparedStatementBinder? = null,
    rowMapper: (row: Row, rowNum: Int) -> T,
): Flow<T> =
    query(psc, psb, rowMapper).asFlow()
