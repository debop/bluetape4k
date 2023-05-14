package io.bluetake4k.data.jdbc

import io.bluetape4k.logging.KLogging
import net.datafaker.Faker

abstract class AbstractJdbcTest {

    companion object: KLogging() {

        @JvmStatic
        val faker = Faker()

        // val mysqlServer = MySQLServer().apply { start() }

    }
}
