package io.bluetape4k.data.cassandra

import com.datastax.oss.driver.api.core.CqlSession
import com.datastax.oss.driver.api.core.CqlSessionBuilder
import io.bluetape4k.logging.KLogging
import io.bluetape4k.testcontainers.storage.Cassandra4Server
import net.datafaker.Faker
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode

@Execution(ExecutionMode.SAME_THREAD)
abstract class AbstractCassandraTest {

    companion object: KLogging() {
        const val DEFAULT_KEYSPACE = "examples"

        val faker = Faker()

        val cassandra4 by lazy {
            Cassandra4Server.Launcher.cassandra4
        }
    }

    // protected val session by lazy { newCqlSession() }
    protected lateinit var session: CqlSession

    protected fun newCqlSession(keyspace: String = DEFAULT_KEYSPACE): CqlSession =
        Cassandra4Server.Launcher.getOrCreateSession(keyspace)

    protected fun newCqlSessionBuilder(): CqlSessionBuilder = Cassandra4Server.Launcher.newCqlSessionBuilder()

    @BeforeAll
    fun beforeAll() {
        session = newCqlSession()
    }

    @AfterAll
    fun afterAll() {
        session.close()
    }
}
