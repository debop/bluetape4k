package io.bluetape4k.testcontainers.jdbc

import io.bluetape4k.logging.KLogging
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode

@Execution(ExecutionMode.SAME_THREAD)
class PostgreSQLServerTest: AbstractJdbcServerTest() {

    companion object: KLogging()

    @Test
    fun `launch PostgreSQL server`() {
        PostgreSQLServer().use { postgres ->
            postgres.start()
            assertConnection(postgres)
        }
    }

    @Test
    fun `launch PostgreSQL server with default port`() {
        PostgreSQLServer(useDefaultPort = true).use { postgres ->
            postgres.start()
            postgres.port shouldBeEqualTo PostgreSQLServer.PORT
            assertConnection(postgres)
        }
    }
}