package io.bluetape4k.spring.cassandra

import com.datastax.oss.driver.api.core.cql.Statement
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.cassandra.core.DeleteOptions
import org.springframework.data.cassandra.core.EntityWriteResult
import org.springframework.data.cassandra.core.InsertOptions
import org.springframework.data.cassandra.core.ReactiveCassandraOperations
import org.springframework.data.cassandra.core.UpdateOptions
import org.springframework.data.cassandra.core.WriteResult
import org.springframework.data.cassandra.core.query.Query
import org.springframework.data.cassandra.core.query.Update
import org.springframework.data.domain.Slice


suspend inline fun <reified T: Any> ReactiveCassandraOperations.countSuspending(): Long? =
    count(T::class.java).awaitSingleOrNull()

suspend inline fun <reified T: Any> ReactiveCassandraOperations.countSuspending(query: Query): Long? =
    count(query, T::class.java).awaitSingleOrNull()

suspend inline fun <reified T: Any> ReactiveCassandraOperations.existsSuspending(id: Any): Boolean? =
    exists(id, T::class.java).awaitSingleOrNull()

suspend inline fun <reified T: Any> ReactiveCassandraOperations.existsSuspending(query: Query): Boolean? =
    exists(query, T::class.java).awaitSingleOrNull()

inline fun <reified T: Any> ReactiveCassandraOperations.selectForFlow(statement: Statement<*>): Flow<T> =
    select(statement, T::class.java).asFlow()

inline fun <reified T: Any> ReactiveCassandraOperations.selectForFlow(cql: String): Flow<T> =
    select(cql, T::class.java).asFlow()

inline fun <reified T: Any> ReactiveCassandraOperations.selectForFlow(query: Query): Flow<T> =
    select(query, T::class.java).asFlow()

suspend inline fun <reified T: Any> ReactiveCassandraOperations.selectOneSuspending(statement: Statement<*>): T? =
    selectOne(statement, T::class.java).awaitSingleOrNull()

suspend inline fun <reified T: Any> ReactiveCassandraOperations.selectOneSuspending(cql: String): T? =
    selectOne(cql, T::class.java).awaitSingleOrNull()

suspend inline fun <reified T: Any> ReactiveCassandraOperations.selectOneSuspending(query: Query): T? =
    selectOne(query, T::class.java).awaitSingleOrNull()

suspend inline fun <reified T: Any> ReactiveCassandraOperations.selectOneByIdSuspending(id: Any): T? =
    selectOneById(id, T::class.java).awaitSingleOrNull()

suspend inline fun <reified T: Any> ReactiveCassandraOperations.sliceSuspending(statement: Statement<*>): Slice<T> =
    slice(statement, T::class.java).awaitSingle()

suspend inline fun <reified T: Any> ReactiveCassandraOperations.sliceSuspending(query: Query): Slice<T> =
    slice(query, T::class.java).awaitSingle()

suspend inline fun <reified T: Any> ReactiveCassandraOperations.truncateSuspending() {
    truncate(T::class.java).awaitSingleOrNull()
}

suspend fun <T: Any> ReactiveCassandraOperations.insertSuspending(entity: T): T? {
    return insert(entity).awaitSingleOrNull()
}

suspend fun <T: Any> ReactiveCassandraOperations.insertSuspending(
    entity: T,
    options: InsertOptions,
): EntityWriteResult<T> {
    return insert(entity, options).awaitSingle()
}

suspend fun <T: Any> ReactiveCassandraOperations.updateSuspending(entity: T): T? {
    return update(entity).awaitSingleOrNull()
}

suspend fun <T: Any> ReactiveCassandraOperations.updateSuspending(
    entity: T,
    options: UpdateOptions,
): EntityWriteResult<T> {
    return update(entity, options).awaitSingle()
}

suspend inline fun <reified T: Any> ReactiveCassandraOperations.updateSuspending(
    query: Query,
    update: Update,
): Boolean? {
    return update(query, update, T::class.java).awaitSingleOrNull()
}

suspend fun <T: Any> ReactiveCassandraOperations.deleteSuspending(entity: T): T? {
    return delete(entity).awaitSingleOrNull()
}

suspend fun <T: Any> ReactiveCassandraOperations.deleteSuspending(
    entity: T,
    options: DeleteOptions,
): WriteResult {
    return delete(entity, options).awaitSingle()
}

suspend inline fun <reified T: Any> ReactiveCassandraOperations.deleteSuspending(query: Query): Boolean? {
    return delete(query, T::class.java).awaitSingleOrNull()
}

suspend inline fun <reified T: Any> ReactiveCassandraOperations.deleteByIdSuspending(id: Any): Boolean? {
    return deleteById(id, T::class.java).awaitSingleOrNull()
}
