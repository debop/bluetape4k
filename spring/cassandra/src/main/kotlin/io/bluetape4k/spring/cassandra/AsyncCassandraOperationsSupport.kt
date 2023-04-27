package io.bluetape4k.spring.cassandra

import com.datastax.oss.driver.api.core.cql.AsyncResultSet
import com.datastax.oss.driver.api.core.cql.Statement
import io.bluetape4k.kotlinx.coroutines.support.awaitSuspending
import org.springframework.data.cassandra.core.AsyncCassandraOperations

suspend fun AsyncCassandraOperations.executeSuspending(stmt: Statement<*>): AsyncResultSet {
    return execute(stmt).awaitSuspending()
}
