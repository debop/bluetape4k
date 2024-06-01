package io.bluetape4k.cassandra.cql

import io.bluetape4k.cassandra.AbstractCassandraTest
import io.bluetape4k.cassandra.toCqlIdentifier
import io.bluetape4k.junit5.coroutines.runSuspendWithIO
import io.bluetape4k.logging.KLogging
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class RowSupportTest: AbstractCassandraTest() {

    companion object: KLogging()

    @BeforeAll
    fun setup() {
        runSuspendWithIO {
            session.executeSuspending("DROP TABLE IF EXISTS row_table")
            session.executeSuspending("CREATE TABLE IF NOT EXISTS row_table (id text PRIMARY KEY, name text, num int);")
            session.executeSuspending("TRUNCATE row_table")

            val ps = session.prepareSuspending("INSERT INTO row_table(id, name, num) VALUES(?, ?, ?)")
            repeat(100) {
                val id = it.toString()
                val name = "name-$id"
                val num = it
                session.executeSuspending(ps.bind(id, name, num))
            }
        }
    }

    @Test
    fun `row to map`() = runSuspendWithIO {
        val row = session.executeSuspending("SELECT * FROM row_table WHERE id=?", "1").one()!!
        row.toMap() shouldBeEqualTo mapOf(0 to "1", 1 to "name-1", 2 to 1)
    }

    @Test
    fun `row to named map`() = runSuspendWithIO {
        val row = session.executeSuspending("SELECT * FROM row_table WHERE id=?", "1").one()!!
        row.toNamedMap() shouldBeEqualTo mapOf("id" to "1", "name" to "name-1", "num" to 1)
    }

    @Test
    fun `row to CqlItentifier map`() = runSuspendWithIO {
        val row = session.executeSuspending("SELECT * FROM row_table WHERE id=?", "1").one()!!
        row.toCqlIdentifierMap() shouldBeEqualTo mapOf(
            "id".toCqlIdentifier() to "1",
            "name".toCqlIdentifier() to "name-1",
            "num".toCqlIdentifier() to 1
        )
    }

    @Test
    fun `get columnCodecs`() = runSuspendWithIO {
        val row = session.executeSuspending("SELECT * FROM row_table WHERE id=?", "1").one()!!

        val columnCodecs = row.columnCodecs()

        columnCodecs.size shouldBeEqualTo 3
        columnCodecs.values.map { it.javaClass.simpleName } shouldBeEqualTo listOf(
            "StringCodec",
            "StringCodec",
            "IntCodec"
        )
    }
}
