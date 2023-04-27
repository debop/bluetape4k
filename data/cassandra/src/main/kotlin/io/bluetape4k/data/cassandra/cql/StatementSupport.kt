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

fun SimpleStatement.toPrepareRequest(): PrepareRequest = DefaultPrepareRequest(this)

fun statementOf(cql: String): SimpleStatement =
    SimpleStatement.newInstance(cql)

fun statementOf(cql: String, vararg positionValues: Any?): SimpleStatement =
    SimpleStatement.newInstance(cql, *positionValues)

fun statementOf(cql: String, nameValues: Map<String, Any?>): SimpleStatement =
    SimpleStatement.newInstance(cql, nameValues)

inline fun buildSimpleStatement(query: String, builder: SimpleStatementBuilder.() -> Unit): SimpleStatement =
    SimpleStatementBuilder(query).apply(builder).build()

inline fun buildBoundStatement(
    boundStatement: BoundStatement,
    setup: BoundStatementBuilder.() -> Unit
): BoundStatement =
    BoundStatementBuilder(boundStatement).apply(setup).build()


fun batchStatementOf(batchType: BatchType): BatchStatement =
    BatchStatement.newInstance(batchType)

fun batchStatementOf(batchType: BatchType, vararg statements: BatchableStatement<*>): BatchStatement =
    BatchStatement.newInstance(batchType, *statements)

fun batchStatementOf(batchType: BatchType, statements: Iterable<BatchableStatement<*>>): BatchStatement =
    BatchStatement.newInstance(batchType, statements)

inline fun buildBatchStatement(batchType: BatchType, setup: BatchStatementBuilder.() -> Unit): BatchStatement =
    BatchStatementBuilder(batchType).apply(setup).build()

inline fun buildBatchStatement(template: BatchStatement, setup: BatchStatementBuilder.() -> Unit): BatchStatement =
    BatchStatementBuilder(template).apply(setup).build()
