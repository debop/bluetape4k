package io.bluetape4k.cassandra

import com.datastax.oss.driver.api.core.CqlSession
import com.datastax.oss.driver.api.core.CqlSessionBuilder
import java.net.InetSocketAddress

inline fun cqlSession(initializer: CqlSessionBuilder.() -> Unit): CqlSession {
    return CqlSessionBuilder().apply(initializer).build()
}

fun cqlSessionOf(
    contactPoint: InetSocketAddress = CqlSessionProvider.DEFAULT_CONTACT_POINT,
    localDatacenter: String = CqlSessionProvider.DEFAULT_LOCAL_DATACENTER,
    keyspaceName: String,
): CqlSession = cqlSession {
    addContactPoint(contactPoint)
    withLocalDatacenter(localDatacenter)
    withKeyspace(keyspaceName)
}
