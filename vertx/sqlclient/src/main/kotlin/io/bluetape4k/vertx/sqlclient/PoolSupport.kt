package io.bluetape4k.vertx.sqlclient

import io.bluetape4k.logging.KotlinLogging
import io.bluetape4k.logging.debug
import io.vertx.kotlin.coroutines.await
import io.vertx.sqlclient.Pool
import io.vertx.sqlclient.SqlConnection
import io.vertx.sqlclient.TransactionRollbackException
import java.sql.SQLException

private val log = KotlinLogging.logger { }

/**
 * Transaction 환경 하에서 Database 작업을 수행합니다.
 *
 * ```
 * val pool = JDBCPool.create(vertx)    // MySQLClient.create(vertx)
 * val rows = pool.withTransactionAndAwait {
 *     SqlTemplate.forQuery("select * from Person where id=#{id}")
 *      .execute(mapOf("id" to 1))
 *      .await()
 * }
 * ```
 *
 * @param action Database 작업
 * @receiver [Pool] 인스턴스
 * @return DB 작업 결과
 */
suspend fun <T> Pool.withTransactionAndAwait(
    action: suspend (conn: SqlConnection) -> T
): T {
    val conn = connection.await()
    val tx = conn.begin().await()
    try {
        val result = action(conn)
        tx.commit().await()
        return result
    } catch (e: TransactionRollbackException) {
        throw (e)
    } catch (e: Throwable) {
        runCatching { tx.rollback().await() }
        throw SQLException(e)
    } finally {
        conn.close().await()
    }
}

/**
 * 테스트 시에 기존 데이터에 영향을 주지 않도록, Tx 작업이 성공하더라도 Rollback을 하도록 합니다.
 *
 * @param T
 * @param action
 * @receiver
 * @return
 */
suspend fun <T> Pool.withRollbackAndAwait(action: suspend (conn: SqlConnection) -> T): T {
    val conn = connection.await()
    log.debug { "Open Connection=$conn" }
    val tx = conn.begin().await()
    try {
        val result = action(conn)
        tx.rollback().await()
        return result
    } catch (e: TransactionRollbackException) {
        throw (e)
    } catch (e: Throwable) {
        runCatching { tx.rollback().await() }
        throw SQLException(e)
    } finally {
        log.debug { "Close connection... conn=$conn" }
        conn.close().await()
    }
}