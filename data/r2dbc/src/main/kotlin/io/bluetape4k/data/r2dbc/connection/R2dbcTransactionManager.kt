package io.bluetape4k.data.r2dbc.connection

import io.r2dbc.spi.ConnectionFactory
import org.springframework.r2dbc.connection.R2dbcTransactionManager

fun r2dbcTransactionManagerOf(connectionFactory: ConnectionFactory): R2dbcTransactionManager =
    R2dbcTransactionManager(connectionFactory)
