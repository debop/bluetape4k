package io.bluetape4k.testcontainers.jdbc

import io.bluetape4k.logging.KLogging
import org.amshove.kluent.shouldBeTrue
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import java.sql.ResultSet

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Execution(ExecutionMode.SAME_THREAD)
abstract class AbstractJdbcServerTest {

    companion object: KLogging()

    protected fun assertConnection(jdbcServer: JdbcServer) {
        jdbcServer.getDataSource().use { ds ->
            ds.connection.use { conn ->
                conn.isValid(1).shouldBeTrue()
            }
        }
    }

    protected fun performQuery(jdbcServer: JdbcServer, sql: String): ResultSet {
        return jdbcServer.getDataSource().use { ds ->
            val stmt = ds.connection.createStatement()
            stmt.execute(sql)
            val resultSet = stmt.resultSet
            resultSet.next()
            resultSet
        }
    }
}
