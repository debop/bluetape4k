package io.bluetape4k.r2dbc.connection

import io.r2dbc.spi.Connection
import io.r2dbc.spi.ConnectionFactory
import io.r2dbc.spi.R2dbcException
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.dao.DataAccessException
import org.springframework.r2dbc.connection.ConnectionFactoryUtils

/**
 * Obtain a {@link Connection} from the given {@link ConnectionFactory}.
 * Translates exceptions into the Spring hierarchy of unchecked generic
 * data access exceptions, simplifying calling code and making any
 * exception that is thrown more meaningful.
 * <p>Is aware of a corresponding Connection bound to the current
 * {@link TransactionSynchronizationManager}. Will bind a Connection to the
 * {@link TransactionSynchronizationManager} if transaction synchronization is active.
 * @param connectionFactory the {@link ConnectionFactory} to obtain
 * {@link Connection Connections} from
 * @return a R2DBC Connection from the given {@link ConnectionFactory}
 * @throws DataAccessResourceFailureException if the attempt to get a
 * {@link Connection} failed
 * @see #releaseConnection
 */
suspend fun ConnectionFactory.getConnection(): Connection? =
    ConnectionFactoryUtils.getConnection(this).awaitFirstOrNull()

/**
 * Actually obtain a R2DBC Connection from the given {@link ConnectionFactory}.
 * Same as {@link #getConnection}, but preserving the original exceptions.
 * <p>Is aware of a corresponding Connection bound to the current
 * {@link TransactionSynchronizationManager}. Will bind a Connection to the
 * {@link TransactionSynchronizationManager} if transaction synchronization is active
 * @param connectionFactory the {@link ConnectionFactory} to obtain Connections from
 * @return a R2DBC {@link Connection} from the given {@link ConnectionFactory}.
 */
suspend fun ConnectionFactory.doGetConnection(): Connection? =
    ConnectionFactoryUtils.doGetConnection(this).awaitFirstOrNull()

/**
 * Actually fetch a {@link Connection} from the given {@link ConnectionFactory}.
 * @param connectionFactory the {@link ConnectionFactory} to obtain
 * {@link Connection}s from
 * @return a R2DBC {@link Connection} from the given {@link ConnectionFactory}
 * (never {@code null}).
 * @throws IllegalStateException if the {@link ConnectionFactory} returned a {@code null} value.
 * @see ConnectionFactory#create()
 */
suspend fun ConnectionFactory.fetchConnection(): Connection? =
    create().awaitFirstOrNull()

/**
 * Close the given {@link Connection}, obtained from the given {@link ConnectionFactory}, if
 * it is not managed externally (that is, not bound to the subscription).
 * @param con the {@link Connection} to close if necessary
 * @param connectionFactory the {@link ConnectionFactory} that the Connection was obtained from
 * @see #getConnection
 */
suspend fun ConnectionFactory.releaseConnection(conn: Connection) {
    ConnectionFactoryUtils.releaseConnection(conn, this).awaitFirstOrNull()
}

/**
 * Actually close the given {@link Connection}, obtained from the given
 * {@link ConnectionFactory}. Same as {@link #releaseConnection},
 * but preserving the original exception.
 * @param connection the {@link Connection} to close if necessary
 * @param connectionFactory the {@link ConnectionFactory} that the Connection was obtained from
 * @see #doGetConnection
 */
suspend fun ConnectionFactory.doReleaseConnection(conn: Connection) {
    ConnectionFactoryUtils.doReleaseConnection(conn, this).awaitFirstOrNull()
}

suspend fun ConnectionFactory.current(): ConnectionFactory {
    return ConnectionFactoryUtils.currentConnectionFactory(this).awaitSingle()
}

fun R2dbcException.convert(task: String, sql: String? = null): DataAccessException? {
    return ConnectionFactoryUtils.convertR2dbcException(task, sql, this)
}

fun Connection.getTargetConnection(): Connection =
    ConnectionFactoryUtils.getTargetConnection(this)
