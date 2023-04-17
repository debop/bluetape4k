package io.bluetape4k.testcontainers.jdbc

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.bluetape4k.testcontainers.GenericServer

/**
 * JDBC Database Server의 기본 속성을 표현하는 Interface
 */
interface JdbcServer: GenericServer {

    fun getDriverClassName(): String

    fun getJdbcUrl(): String

    fun getUsername(): String?

    fun getPassword(): String?

    fun getDatabaseName(): String?

}

/**
 * [JdbcServer]의 Jdbc properties를 생성합니다.
 */
fun <T: JdbcServer> T.buildJdbcProperties(): Map<String, Any?> {
    return mapOf(
        "driver-class-name" to getDriverClassName(),
        "jdbc-url" to getJdbcUrl(),
        "username" to getUsername(),
        "password" to getPassword(),
        "database" to getDatabaseName()
    )
}

/**
 * Database 접속을 위한 [HikariDataSource]를 제공합니다.
 */
fun <T: JdbcServer> T.getDataSource(): HikariDataSource {
    val config = HikariConfig().also {
        it.driverClassName = getDriverClassName()
        it.jdbcUrl = getJdbcUrl()
        it.username = getUsername()
        it.password = getPassword()
    }
    return HikariDataSource(config)
}
