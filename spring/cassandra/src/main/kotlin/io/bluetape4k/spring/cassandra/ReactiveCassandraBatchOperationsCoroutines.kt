package io.bluetape4k.spring.cassandra

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactor.mono
import org.springframework.data.cassandra.core.ReactiveCassandraBatchOperations
import org.springframework.data.cassandra.core.cql.WriteOptions

fun ReactiveCassandraBatchOperations.insertFlow(entities: Flow<*>): ReactiveCassandraBatchOperations {
    return insert(mono { entities.toList() })
}

fun ReactiveCassandraBatchOperations.insertFlow(
    entities: Flow<*>,
    options: WriteOptions,
): ReactiveCassandraBatchOperations {
    return insert(mono { entities.toList() }, options)
}

fun ReactiveCassandraBatchOperations.updateFlow(entities: Flow<*>): ReactiveCassandraBatchOperations {
    return update(mono { entities.toList() })
}

fun ReactiveCassandraBatchOperations.updateFlow(
    entities: Flow<*>,
    options: WriteOptions,
): ReactiveCassandraBatchOperations {
    return update(mono { entities.toList() }, options)
}

fun ReactiveCassandraBatchOperations.deleteFlow(entities: Flow<*>): ReactiveCassandraBatchOperations {
    return delete(mono { entities.toList() })
}

fun ReactiveCassandraBatchOperations.deleteFlow(
    entities: Flow<*>,
    options: WriteOptions,
): ReactiveCassandraBatchOperations {
    return delete(mono { entities.toList() }, options)
}
