package io.bluetape4k.vertx.sqlclient

import io.bluetape4k.core.requireNotBlank
import io.bluetape4k.io.utils.Resourcex
import io.bluetape4k.junit5.coroutines.runSuspendTest
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.info
import io.bluetape4k.testcontainers.jdbc.MySQL8Server
import io.vertx.core.Vertx
import io.vertx.jdbcclient.JDBCConnectOptions
import io.vertx.jdbcclient.JDBCPool
import io.vertx.junit5.VertxExtension
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import io.vertx.kotlin.jdbcclient.jdbcConnectOptionsOf
import io.vertx.kotlin.mysqlclient.mySQLConnectOptionsOf
import io.vertx.kotlin.sqlclient.poolOptionsOf
import io.vertx.mysqlclient.MySQLConnectOptions
import io.vertx.mysqlclient.MySQLPool
import io.vertx.sqlclient.Pool
import io.vertx.sqlclient.PoolOptions
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.extension.ExtendWith
import kotlin.coroutines.CoroutineContext

@ExtendWith(VertxExtension::class)
abstract class AbstractVertxSqlClientTest {


    companion object : KLogging() {

        /**
         * Testcontainers 를 이용해 Loading 시 시간이 걸려서 Connection timeout 이 생깁니다. 무시하셔도 됩니다.
         */
        val mysql: MySQL8Server by lazy { MySQL8Server.Launcher.mysql }

        val MySQL8Server.connectOptions: MySQLConnectOptions
            get() = mySQLConnectOptionsOf(
                host = host,
                port = port,
                database = databaseName,
                user = username,
                password = password,
                tcpKeepAlive = true
            )

        val h2ConnectOptions: JDBCConnectOptions by lazy {
            jdbcConnectOptionsOf(
                jdbcUrl = "jdbc:h2:mem:test;MODE=MYSQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;DATABASE_TO_UPPER=FALSE;",
                user = "sa"
            )
        }

        private val defaultPoolOptions: PoolOptions = poolOptionsOf() // (maxSize = 4, shared = true, eventLoopSize = 2)

        fun Vertx.getMySQLPool(
            connectOptions: MySQLConnectOptions = mysql.connectOptions,
            poolOptions: PoolOptions = defaultPoolOptions,
        ): MySQLPool {
            connectOptions.host.requireNotBlank("host")
            return MySQLPool.pool(this, connectOptions, poolOptions)
        }

        fun Vertx.getH2Pool(
            connectOptions: JDBCConnectOptions = h2ConnectOptions,
            poolOptions: PoolOptions = defaultPoolOptions,
        ): JDBCPool {
            return JDBCPool.pool(this, connectOptions, poolOptions)
        }
    }

    protected open fun Vertx.getPool(): Pool = getH2Pool() // getMySQLPool()

    protected abstract val schemaFileNames: List<String>

    protected lateinit var pool: Pool

    @BeforeAll
    fun setup(vertx: Vertx) = runSuspendTest(vertx.dispatcher() as CoroutineContext) {
        pool = vertx.getPool()

        log.info { "Initialize database" }
        val dbType = if (pool is MySQLPool) "mysql" else "h2"
        pool.withTransactionAndAwait { conn ->
            schemaFileNames.forEach { path ->
                val query = Resourcex.getString("mybatis/schema/$dbType/$path")
                conn.query(query).execute().await()
            }
        }
    }

    @AfterAll
    fun afterAll() {
        if (this::pool.isInitialized) {
            runBlocking { pool.close().await() }
        }
    }
}
