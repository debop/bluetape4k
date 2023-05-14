package io.bluetape4k.spring.cassandra

import com.datastax.oss.driver.api.core.CqlSession
import com.datastax.oss.driver.api.core.Version
import io.bluetape4k.junit5.faker.Fakers
import io.bluetape4k.logging.KLogging
import io.bluetape4k.testcontainers.storage.Cassandra4Server
import io.bluetape4k.testcontainers.storage.getCassandraReleaseVersion
import org.springframework.beans.factory.annotation.Autowired

abstract class AbstractCassandraTest {

    companion object: KLogging() {
        const val DEFAULT_KEYSPACE = "examples"

        val faker = Fakers.faker
    }

    @Autowired
    protected lateinit var session: CqlSession

    protected fun createKeyspace(keyspace: String) {
        Cassandra4Server.Launcher.createKeyspace(session, keyspace)
    }

    protected fun dropKeyspace(keyspace: String) {
        Cassandra4Server.Launcher.dropKeyspace(session, keyspace)
    }

    protected fun getCassandraVersion(session: CqlSession): Version? {
        return session.getCassandraReleaseVersion()
    }
}
