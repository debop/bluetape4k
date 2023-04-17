package io.bluetape4k.testcontainers.jdbc

import io.bluetape4k.logging.KLogging
import org.amshove.kluent.shouldBeTrue

abstract class AbstractJdbcServerTest {

    companion object: KLogging()

    protected fun assertConnection(jdbcServer: JdbcServer) {
        jdbcServer.getDataSource().use { ds ->
            ds.connection.use { conn ->
                conn.isValid(1).shouldBeTrue()
            }
        }
    }
}
