package io.bluetape4k.testcontainers.jdbc

import io.bluetape4k.logging.KLogging
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test

class PostgreSQLServerTest: AbstractJdbcServerTest() {

    companion object: KLogging()

    @Test
    fun `launch PostgreSQL server`() {
        PostgreSQLServer().use { postgres ->
            postgres.start()
            Thread.sleep(10)
            assertConnection(postgres)
        }
    }

    @Test
    fun `launch PostgreSQL server with default port`() {
        PostgreSQLServer(useDefaultPort = true).use { postgres ->
            postgres.start()
            Thread.sleep(100)
            postgres.port shouldBeEqualTo PostgreSQLServer.PORT
            assertConnection(postgres)
        }
    }
}
