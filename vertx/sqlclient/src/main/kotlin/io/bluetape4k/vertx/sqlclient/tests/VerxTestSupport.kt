package io.bluetape4k.vertx.sqlclient.tests

import io.bluetape4k.vertx.sqlclient.withRollbackSuspending
import io.bluetape4k.vertx.sqlclient.withTransactionSuspending
import io.vertx.core.Vertx
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.dispatcher
import io.vertx.sqlclient.Pool
import io.vertx.sqlclient.SqlConnection
import kotlinx.coroutines.runBlocking

fun Vertx.testWithTransactionSuspending(
    testContext: VertxTestContext,
    pool: Pool,
    block: suspend (conn: SqlConnection) -> Unit,
) {
    runBlocking(dispatcher()) {
        try {
            pool.withTransactionSuspending(block)
            testContext.completeNow()
        } catch (e: Throwable) {
            testContext.failNow(e)
        }
    }
}

fun Vertx.testWithRollbackSuspending(
    testContext: VertxTestContext,
    pool: Pool,
    block: suspend (conn: SqlConnection) -> Unit,
) {
    runBlocking(dispatcher()) {
        try {
            pool.withRollbackSuspending(block)
            testContext.completeNow()
        } catch (e: Throwable) {
            testContext.failNow(e)
        }
    }
}
