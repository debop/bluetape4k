package io.bluetape4k.spring.cassandra

import com.datastax.oss.driver.api.core.cql.PreparedStatement
import com.datastax.oss.driver.api.core.cql.SimpleStatement
import com.datastax.oss.driver.api.core.cql.Statement
import io.bluetape4k.core.requireNotBlank
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.data.cassandra.ReactiveResultSet
import org.springframework.data.cassandra.ReactiveSession

/**
 * [cql]를 [ReactiveSession]을 통해 실행합니다.
 *
 * @param cql 실행할 CQL 문자열
 * @param values 인자 값
 * @return [ReactiveResultSet] instance
 */
suspend fun ReactiveSession.executeSuspending(cql: String, vararg values: Any?): ReactiveResultSet {
    cql.requireNotBlank("cql")
    return execute(cql, *values).awaitSingle()
}

/**
 * [cql]를 [ReactiveSession]을 통해 실행합니다.
 *
 * @param cql 실행할 CQL 문자열
 * @param values parameter name-value map
 * @return [ReactiveResultSet] instance
 */
suspend fun ReactiveSession.executeSuspending(cql: String, values: Map<String, Any?>): ReactiveResultSet {
    cql.requireNotBlank("cql")
    return execute(cql, values).awaitSingle()
}

/**
 * [statement]를 [ReactiveSession]을 통해 실행합니다.
 *
 * @param statement 실행할 [Statement]
 * @return [ReactiveResultSet] instance
 */
suspend fun ReactiveSession.executeSuspending(statement: Statement<*>): ReactiveResultSet {
    return execute(statement).awaitSingle()
}

/**
 * [query]를 이용하여 [PreparedStatement]를 빌드합니다.
 *
 * @param query [PreparedStatement]를 만들 Query string
 * @return [PreparedStatement] instance
 */
suspend fun ReactiveSession.prepareSuspending(query: String): PreparedStatement {
    return prepare(query).awaitSingle()
}

/**
 * [SimpleStatement]를 이용하여 [PreparedStatement]를 빌드합니다.
 *
 * @param statement [PreparedStatement]를 만들 [SimpleStatement]
 * @return [PreparedStatement] instance
 */
suspend fun ReactiveSession.prepareSuspending(statement: SimpleStatement): PreparedStatement {
    return prepare(statement).awaitSingle()
}
