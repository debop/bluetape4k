package io.bluetape4k.data.cassandra.cql

import com.datastax.oss.driver.api.core.CqlSession
import com.datastax.oss.driver.api.core.cql.AsyncCqlSession
import com.datastax.oss.driver.api.core.cql.AsyncResultSet
import com.datastax.oss.driver.api.core.cql.PreparedStatement
import com.datastax.oss.driver.api.core.cql.SimpleStatement
import com.datastax.oss.driver.api.core.cql.Statement
import com.datastax.oss.driver.internal.core.cql.DefaultPrepareRequest
import kotlinx.coroutines.future.await

/**
 * [CqlSession]을 이용하여 `query`를 실행할 때, suspend 함수로 실행하도록 합니다.
 *
 * @param cql  실행할 Query statement
 * @param values parameter 값
 * @return [AsyncResultSet] 인스턴스
 */
suspend fun AsyncCqlSession.executeSuspending(cql: String, vararg values: Any?): AsyncResultSet {
    return executeAsync(statementOf(cql, *values)).await()
}

/**
 * [CqlSession]을 이용하여 `query`를 실행할 때, suspend 함수로 실행하도록 합니다.
 *
 * @param cql  실행할 Query statement
 * @param values parameter name-value map
 * @return [AsyncResultSet] 인스턴스
 */
suspend fun AsyncCqlSession.executeSuspending(cql: String, values: Map<String, Any?>): AsyncResultSet {
    return executeAsync(statementOf(cql, values)).await()
}

/**
 * [CqlSession]을 이용하여 `query`를 실행할 때, suspend 함수로 실행하도록 합니다.
 *
 * @param cql  실행할 Query statement
 * @param values parameter name-value map
 * @return [AsyncResultSet] 인스턴스
 */
suspend fun AsyncCqlSession.executeSuspending(statement: Statement<*>): AsyncResultSet {
    return executeAsync(statement).await()
}


/**
 * [PreparedStatement]를 준비합니다.
 *
 * @param cql 실행할 cassandra query
 * @return [AsyncResultSet] 인스턴스
 */
suspend fun AsyncCqlSession.prepareSuspending(cql: String): PreparedStatement {
    return prepareAsync(DefaultPrepareRequest(cql)).await()
}

/**
 * [PreparedStatement]를 준비합니다.
 *
 * @param statement 실행할 statement
 * @return [AsyncResultSet] 인스턴스
 */
suspend fun AsyncCqlSession.prepareSuspending(statement: SimpleStatement): PreparedStatement {
    return prepareAsync(DefaultPrepareRequest(statement)).await()
}
