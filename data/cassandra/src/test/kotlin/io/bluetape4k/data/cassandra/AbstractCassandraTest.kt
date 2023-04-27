package io.bluetape4k.data.cassandra

import com.datastax.oss.driver.api.core.CqlSession
import com.datastax.oss.driver.api.core.CqlSessionBuilder
import io.bluetape4k.logging.KLogging
import io.bluetape4k.testcontainers.storage.Cassandra4Server
import net.datafaker.Faker

abstract class AbstractCassandraTest {

    companion object: KLogging() {
        const val DEFAULT_KEYSPACE = "examples"

        val faker = Faker()

        val cassandra4 by lazy {
            Cassandra4Server.Launcher.cassandra4
        }
    }

    protected val session by lazy { newCqlSession() }

    protected fun newCqlSession(keyspace: String = DEFAULT_KEYSPACE): CqlSession =
        Cassandra4Server.Launcher.getOrCreateSession(keyspace)

    protected fun newCqlSessionBuilder(): CqlSessionBuilder = Cassandra4Server.Launcher.newCqlSessionBuilder()
}
