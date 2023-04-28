package io.bluetape4k.data.cassandra.cql

import com.datastax.oss.driver.api.core.cql.BatchStatement
import com.datastax.oss.driver.api.core.cql.BatchStatementBuilder
import com.datastax.oss.driver.api.core.cql.BatchType
import com.datastax.oss.driver.api.core.cql.BatchableStatement
import com.datastax.oss.driver.api.core.cql.BoundStatement
import com.datastax.oss.driver.api.core.cql.BoundStatementBuilder
import com.datastax.oss.driver.api.core.cql.PrepareRequest
import com.datastax.oss.driver.api.core.cql.SimpleStatement
import com.datastax.oss.driver.api.core.cql.SimpleStatementBuilder
import com.datastax.oss.driver.internal.core.cql.DefaultPrepareRequest
import io.bluetape4k.core.requireNotBlank


fun SimpleStatement.toPrepareRequest(): PrepareRequest = DefaultPrepareRequest(this)

inline fun simpleStatement(query: String, initializer: SimpleStatementBuilder.() -> Unit): SimpleStatement {
    query.requireNotBlank("query")
    return SimpleStatement.builder(query).apply(initializer).build()
}

fun statementOf(cql: String): SimpleStatement {
    cql.requireNotBlank("cql")
    return SimpleStatement.newInstance(cql)
}

fun statementOf(cql: String, vararg positionValues: Any?): SimpleStatement {
    cql.requireNotBlank("cql")
    return SimpleStatement.newInstance(cql, *positionValues)
}

fun statementOf(cql: String, nameValues: Map<String, Any?>): SimpleStatement {
    cql.requireNotBlank("cql")
    return SimpleStatement.newInstance(cql, nameValues)
}


inline fun boundStatement(
    boundStatement: BoundStatement,
    initializer: BoundStatementBuilder.() -> Unit
): BoundStatement {
    return BoundStatementBuilder(boundStatement).apply(initializer).build()
}

fun batchStatementOf(batchType: BatchType): BatchStatement {
    return BatchStatement.newInstance(batchType)
}

fun batchStatementOf(batchType: BatchType, vararg statements: BatchableStatement<*>): BatchStatement {
    return BatchStatement.newInstance(batchType, *statements)
}

fun batchStatementOf(batchType: BatchType, statements: Iterable<BatchableStatement<*>>): BatchStatement {
    return BatchStatement.newInstance(batchType, statements)
}

inline fun batchStatement(batchType: BatchType, initializer: BatchStatementBuilder.() -> Unit): BatchStatement {
    return BatchStatementBuilder(batchType).apply(initializer).build()
}

inline fun batchStatement(template: BatchStatement, initializer: BatchStatementBuilder.() -> Unit): BatchStatement {
    return BatchStatementBuilder(template).apply(initializer).build()
}
