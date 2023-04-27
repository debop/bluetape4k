package io.bluetape4k.data.cassandra

import com.datastax.oss.driver.api.core.CqlSession
import com.datastax.oss.driver.api.core.CqlSessionBuilder

inline fun cqlSession(initializer: CqlSessionBuilder.() -> Unit): CqlSession {
    return CqlSessionBuilder().apply(initializer).build()
}
