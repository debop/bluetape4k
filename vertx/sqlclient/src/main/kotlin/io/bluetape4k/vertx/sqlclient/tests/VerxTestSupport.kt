package io.bluetape4k.vertx.sqlclient.tests

import io.bluetape4k.vertx.sqlclient.withRollbackAndAwait
import io.bluetape4k.vertx.sqlclient.withTransactionAndAwait
import io.bluetape4k.vertx.withVertxDispatcher
import io.vertx.core.Vertx
import io.vertx.junit5.VertxTestContext
import io.vertx.sqlclient.Pool
import io.vertx.sqlclient.SqlConnection

suspend fun Vertx.testWithTransaction(
    testContext: VertxTestContext,
    pool: Pool,
    block: suspend (conn: SqlConnection) -> Unit
) {
    return withVertxDispatcher {
        try {
            pool.withTransactionAndAwait(block)
            testContext.completeNow()
        } catch (e: Throwable) {
            testContext.failNow(e)
        }
    }
}

suspend fun Vertx.testWithRollback(
    testContext: VertxTestContext,
    pool: Pool,
    block: suspend (conn: SqlConnection) -> Unit
) {
    return withVertxDispatcher {
        try {
            pool.withRollbackAndAwait(block)
            testContext.completeNow()
        } catch (e: Throwable) {
            testContext.failNow(e)
        }
    }
}
