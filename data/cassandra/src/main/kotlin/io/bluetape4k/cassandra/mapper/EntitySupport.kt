package io.bluetape4k.cassandra.mapper

import com.datastax.oss.driver.api.core.CqlSession
import com.datastax.oss.driver.api.core.cql.BoundStatement
import com.datastax.oss.driver.api.core.cql.BoundStatementBuilder
import com.datastax.oss.driver.api.core.cql.PreparedStatement
import com.datastax.oss.driver.api.mapper.entity.EntityHelper
import com.datastax.oss.driver.api.mapper.entity.saving.NullSavingStrategy

fun <T: Any> EntityHelper<T>.prepareInsert(session: CqlSession): PreparedStatement {
    return session.prepare(insert().asCql())
}

fun <T: Any> EntityHelper<T>.prepareInsertIfNotExists(session: CqlSession): PreparedStatement {
    return session.prepare(insert().ifNotExists().asCql())
}

inline fun <T: Any> bindEntity(
    preparedStatement: PreparedStatement,
    initializer: BoundStatementBuilder.() -> Unit,
): BoundStatement {
    return preparedStatement.boundStatementBuilder()
        .apply(initializer)
        .build()
}

fun <T: Any> EntityHelper<T>.bind(
    preparedStatement: PreparedStatement,
    entity: T,
    nullSavingStrategy: NullSavingStrategy = NullSavingStrategy.DO_NOT_SET,
    lenient: Boolean = true,
): BoundStatement {
    return preparedStatement.boundStatementBuilder()
        .apply {
            set(entity, this, nullSavingStrategy, lenient)
        }
        .build()
}

inline fun <T: Any> CqlSession.prepare(
    entityHelper: EntityHelper<T>,
    block: EntityHelper<T>.() -> String,
): PreparedStatement {
    return prepare(block(entityHelper))
}
