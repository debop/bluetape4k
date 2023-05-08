package io.bluetape4k.workshop.sqlclient

import io.bluetape4k.logging.KLogging
import io.bluetape4k.testcontainers.jdbc.MySQL8Server
import io.vertx.core.Vertx
import io.vertx.jdbcclient.JDBCConnectOptions
import io.vertx.jdbcclient.JDBCPool
import io.vertx.junit5.VertxExtension
import io.vertx.kotlin.jdbcclient.jdbcConnectOptionsOf
import io.vertx.kotlin.mysqlclient.mySQLConnectOptionsOf
import io.vertx.kotlin.sqlclient.poolOptionsOf
import io.vertx.mysqlclient.MySQLConnectOptions
import io.vertx.mysqlclient.MySQLPool
import io.vertx.sqlclient.PoolOptions
import net.datafaker.Faker
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(VertxExtension::class)
abstract class AbstractSqlClientTest {

    companion object: KLogging() {

        val faker = Faker()

        private val mysql by lazy { MySQL8Server.Launcher.mysql }

        val MySQL8Server.connectOptions: MySQLConnectOptions
            get() = mySQLConnectOptionsOf(
                host = host,
                port = port,
                database = databaseName,
                user = username,
                password = password
            )

        private val h2ConnectOptions: JDBCConnectOptions by lazy {
            jdbcConnectOptionsOf(
                jdbcUrl = "jdbc:h2:mem:test;MODE=MYSQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;DATABASE_TO_UPPER=FALSE;",
                user = "sa"
            )
        }

        private val defaultPoolOptions = poolOptionsOf(maxSize = 20)

        fun Vertx.getMySQLPool(
            connectOptions: MySQLConnectOptions = mysql.connectOptions,
            poolOptions: PoolOptions = defaultPoolOptions,
        ): MySQLPool =
            MySQLPool.pool(this, connectOptions, poolOptions)

        fun Vertx.getH2Pool(
            connectOptions: JDBCConnectOptions = h2ConnectOptions,
            poolOptions: PoolOptions = defaultPoolOptions,
        ): JDBCPool =
            JDBCPool.pool(this, connectOptions, poolOptions)
    }
}
