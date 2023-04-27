package io.bluetape4k.data.cassandra.cql

import io.bluetape4k.concurrent.sequence
import io.bluetape4k.data.cassandra.AbstractCassandraTest
import io.bluetape4k.junit5.coroutines.runSuspendWithIO
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.future.await
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldNotBeEmpty
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.io.Serializable

class AsyncResultSetSupportTest: AbstractCassandraTest() {

    companion object: KLogging() {
        private const val SIZE = 6000
    }

    @BeforeAll
    fun setup() {
        runSuspendWithIO {
            session.executeSuspending("DROP TABLE IF EXISTS bulks")
            session.executeSuspending("CREATE TABLE IF NOT EXISTS bulks (id text PRIMARY KEY, name text);")
            session.executeSuspending("TRUNCATE bulks")

            val ps = session.prepareSuspending("INSERT INTO bulks(id, name) VALUES(?, ?)")
            val futures = List(SIZE) {
                val id = it.toString()
                val name = faker.name().username()

                session.executeAsync(ps.bind(id, name))
            }
            futures.sequence().await()
        }
    }

    @Test
    fun `load as flow`() = runSuspendWithIO {
        log.debug { "Load all bulks" }
        var count = 0

        val flow = session.executeSuspending("SELECT * FROM bulks").asFlow()
        flow
            .buffer()
            .onEach { row ->
                count++
                val id = row.getStringOrEmpty(0)
                val name = row.getStringOrEmpty(1)

                id.shouldNotBeEmpty()
                name.shouldNotBeEmpty()
            }
            .collect()

        log.debug { "Loaded record count=$count" }
        count shouldBeEqualTo SIZE
    }

    data class Bulk(val id: String, val name: String): Serializable

    @Test
    fun `load as flow with row mapper`() = runSuspendWithIO {
        log.debug { "Load all bulks" }
        var count = 0
        val flow = session
            .executeSuspending("SELECT * FROM bulks")
            .asFlow { row -> Bulk(row.getStringOrEmpty(0), row.getStringOrEmpty(1)) }

        flow.buffer()
            .onEach { bulk ->
                count++
                bulk.id.shouldNotBeEmpty()
                bulk.name.shouldNotBeEmpty()
            }
            .collect()

        log.debug { "Loaded record count=$count" }
        count shouldBeEqualTo SIZE
    }
}
