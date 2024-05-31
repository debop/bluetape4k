package io.bluetape4k.testcontainers.jdbc

import io.bluetape4k.logging.KLogging
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test

class ClickHouseServerTest: AbstractJdbcServerTest() {

    companion object: KLogging()

    @Test
    fun `launch ClickHouse Server`() {
        ClickHouseServer().use { clickhouse ->
            clickhouse.start()
            assertConnection(clickhouse)
        }
    }

    @Test
    fun `launch ClickHouse Server with default port`() {
        ClickHouseServer(useDefaultPort = true).use { clickhouse ->
            clickhouse.start()
            clickhouse.port shouldBeEqualTo ClickHouseServer.HTTP_PORT
            assertConnection(clickhouse)
        }
    }
}
