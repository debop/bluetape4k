package io.bluetape4k.data.r2dbc.connection.init

import io.r2dbc.spi.ConnectionFactory
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer

inline fun connectionFactoryInitializer(
    connectionFactory: ConnectionFactory,
    initializer: ConnectionFactoryInitializer.() -> Unit,
): ConnectionFactoryInitializer {
    return ConnectionFactoryInitializer().apply {
        setConnectionFactory(connectionFactory)
        initializer()
    }
}
