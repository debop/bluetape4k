package io.bluetake4k.data.jdbc

import io.bluetape4k.junit5.faker.Fakers
import io.bluetape4k.logging.KLogging

abstract class AbstractJdbcTest {

    companion object: KLogging() {

        @JvmStatic
        val faker = Fakers.faker

        // val mysqlServer = MySQLServer().apply { start() }

    }
}
