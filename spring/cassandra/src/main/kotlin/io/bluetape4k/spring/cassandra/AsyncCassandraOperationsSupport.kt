package io.bluetape4k.spring.cassandra

import com.datastax.oss.driver.api.core.cql.AsyncResultSet
import com.datastax.oss.driver.api.core.cql.Statement
import io.bluetape4k.data.cassandra.cql.statementOf
import kotlinx.coroutines.future.await
import org.springframework.data.cassandra.core.AsyncCassandraOperations
import org.springframework.data.cassandra.core.DeleteOptions
import org.springframework.data.cassandra.core.EntityWriteResult
import org.springframework.data.cassandra.core.InsertOptions
import org.springframework.data.cassandra.core.UpdateOptions
import org.springframework.data.cassandra.core.WriteResult
import org.springframework.data.cassandra.core.query.Query
import org.springframework.data.cassandra.core.query.Update
import org.springframework.data.domain.Slice
import org.springframework.data.domain.SliceImpl

suspend fun AsyncCassandraOperations.executeSuspending(stmt: Statement<*>): AsyncResultSet {
    return execute(stmt).await()
}

suspend inline fun <reified T: Any> AsyncCassandraOperations.selectSuspending(statement: Statement<*>): List<T> =
    select(statement, T::class.java).await() ?: emptyList()

suspend inline fun <reified T: Any> AsyncCassandraOperations.selectSuspending(
    statement: Statement<*>,
    crossinline consumer: (T) -> Unit,
) {
    select(statement, { consumer(it) }, T::class.java).await()
}

suspend inline fun <reified T: Any> AsyncCassandraOperations.selectSuspending(cql: String): List<T> {
    return selectSuspending(statementOf(cql))
}

suspend inline fun <reified T: Any> AsyncCassandraOperations.selectSuspending(
    cql: String,
    crossinline consumer: (T) -> Unit,
) {
    selectSuspending(statementOf(cql), consumer)
}

suspend inline fun <reified T: Any> AsyncCassandraOperations.selectSuspending(
    query: Query,
    crossinline consumer: (T) -> Unit,
) {
    select(query, { consumer(it) }, T::class.java).await()
}

suspend inline fun <reified T: Any> AsyncCassandraOperations.selectOneSuspending(statement: Statement<*>): T? {
    return selectOne(statement, T::class.java).await()
}

suspend inline fun <reified T: Any> AsyncCassandraOperations.selectOneSuspending(cql: String): T? {
    return selectOneSuspending(statementOf(cql))
}

suspend inline fun <reified T: Any> AsyncCassandraOperations.selectSuspending(query: Query): List<T> =
    select(query, T::class.java).await() ?: emptyList()

suspend inline fun <reified T: Any> AsyncCassandraOperations.selectOneSuspending(query: Query): T? =
    selectOne(query, T::class.java).await()

suspend inline fun <reified T: Any> AsyncCassandraOperations.sliceSuspending(statement: Statement<*>): Slice<T> =
    slice(statement, T::class.java).await() ?: SliceImpl(emptyList())

suspend inline fun <reified T: Any> AsyncCassandraOperations.sliceSuspending(query: Query): Slice<T> =
    slice(query, T::class.java).await() ?: SliceImpl(emptyList())

suspend inline fun <reified T: Any> AsyncCassandraOperations.updateSuspending(query: Query, update: Update): Boolean? =
    update(query, update, T::class.java).await()

suspend inline fun <reified T: Any> AsyncCassandraOperations.deleteSuspending(query: Query): Boolean? =
    delete(query, T::class.java).await()

suspend inline fun <reified T: Any> AsyncCassandraOperations.countSuspending(): Long? =
    count(T::class.java).await()


suspend inline fun <reified T: Any> AsyncCassandraOperations.countSuspending(query: Query): Long? =
    count(query, T::class.java).await()


suspend inline fun <reified T: Any> AsyncCassandraOperations.existsSuspending(id: Any): Boolean? =
    exists(id, T::class.java).await()

suspend inline fun <reified T: Any> AsyncCassandraOperations.existsSuspending(query: Query): Boolean? =
    exists(query, T::class.java).await()

suspend inline fun <reified T: Any> AsyncCassandraOperations.selectOneByIdSuspending(id: Any): T? =
    selectOneById(id, T::class.java).await()

suspend inline fun <reified T: Any> AsyncCassandraOperations.deleteByIdSuspending(id: Any): Boolean? =
    deleteById(id, T::class.java).await()

suspend inline fun <reified T: Any> AsyncCassandraOperations.truncateSuspending() {
    truncate(T::class.java).await()
}

suspend fun <T: Any> AsyncCassandraOperations.insertSuspending(entity: T): T? =
    insert(entity).await()

suspend fun <T: Any> AsyncCassandraOperations.insertSuspending(
    entity: T,
    options: InsertOptions,
): EntityWriteResult<T> =
    insert(entity, options).await()

suspend fun <T: Any> AsyncCassandraOperations.updateSuspending(entity: T): T? =
    update(entity).await()

suspend fun <T: Any> AsyncCassandraOperations.updateSuspending(
    entity: T,
    options: UpdateOptions,
): EntityWriteResult<T> =
    update(entity, options).await()

suspend fun <T: Any> AsyncCassandraOperations.deleteSuspending(entity: T): T? =
    delete(entity).await()

suspend fun AsyncCassandraOperations.deleteSuspending(entity: Any, options: DeleteOptions): WriteResult =
    delete(entity, options).await()
