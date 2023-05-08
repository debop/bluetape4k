package io.bluetape4k.spring.cassandra.reactive

import com.datastax.oss.driver.api.core.type.DataTypes
import com.datastax.oss.driver.api.querybuilder.QueryBuilder
import com.datastax.oss.driver.api.querybuilder.QueryBuilder.bindMarker
import com.datastax.oss.driver.api.querybuilder.QueryBuilder.insertInto
import com.datastax.oss.driver.api.querybuilder.QueryBuilder.selectFrom
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder
import io.bluetape4k.data.cassandra.querybuilder.bindMarker
import io.bluetape4k.data.cassandra.querybuilder.eq
import io.bluetape4k.data.cassandra.querybuilder.literal
import io.bluetape4k.junit5.coroutines.runSuspendWithIO
import io.bluetape4k.logging.KLogging
import io.bluetape4k.spring.cassandra.AbstractCassandraCoroutineTest
import io.bluetape4k.spring.cassandra.executeSuspending
import io.bluetape4k.spring.cassandra.prepareSuspending
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.cassandra.ReactiveSession

@SpringBootTest(classes = [ReactiveTestConfiguration::class])
class ReactiveSessionCoroutinesExamples(
    @Autowired private val reactiveSession: ReactiveSession,
): AbstractCassandraCoroutineTest("reactive-session") {

    companion object: KLogging() {
        private const val ACTOR_TABLE_NAME = "reactive_session_coroutines_actor"
        private val initialized = atomic(false)
    }

    data class Actor(
        val id: Long? = null,
        val first_name: String? = null,
        val last_name: String? = null,
    )

    @BeforeEach
    fun setup() {
        runSuspendWithIO {
            with(reactiveSession) {
                if (initialized.compareAndSet(false, true)) {
                    executeSuspending(SchemaBuilder.dropTable(ACTOR_TABLE_NAME).ifExists().build())

                    executeSuspending(
                        SchemaBuilder.createTable(ACTOR_TABLE_NAME)
                            .ifNotExists()
                            .withPartitionKey("id", DataTypes.BIGINT)
                            .withColumn("last_name", DataTypes.TEXT)
                            .withColumn("first_name", DataTypes.TEXT)
                            .build()
                    )
                }

                executeSuspending(QueryBuilder.truncate(ACTOR_TABLE_NAME).build())
                executeSuspending(
                    insertInto(ACTOR_TABLE_NAME)
                        .value("id", 1212L.literal())
                        .value("first_name", "Joe".literal())
                        .value("last_name", "Biden".literal())
                        .build()
                )
                executeSuspending(
                    QueryBuilder.insertInto(ACTOR_TABLE_NAME)
                        .value("id", 4242L.literal())
                        .value("first_name", "Debop".literal())
                        .value("last_name", "Bae".literal())
                        .build()
                )
            }
        }
    }

    @Test
    fun `execute cql in coroutines`() = runSuspendWithIO {
        val cql = selectFrom(ACTOR_TABLE_NAME)
            .all()
            .whereColumn("id").eq(bindMarker())
            .asCql()
        val rrset = reactiveSession.executeSuspending(cql, 1212L)
        val rows = rrset.rows().asFlow().toList()
        rows.size shouldBeEqualTo 1
        val row = rows.first()
        row.getString("first_name") shouldBeEqualTo "Joe"
        row.getString("last_name") shouldBeEqualTo "Biden"
    }

    @Test
    fun `execute cql with map in coroutines`() = runSuspendWithIO {
        val cql = selectFrom(ACTOR_TABLE_NAME)
            .all()
            .whereColumn("id").eq("id".bindMarker())
            .asCql()
        val rrset = reactiveSession.executeSuspending(cql, mapOf("id" to 1212L))
        val rows = rrset.rows().asFlow().toList()
        rows.size shouldBeEqualTo 1
        val row = rows.first()
        row.getString("first_name") shouldBeEqualTo "Joe"
        row.getString("last_name") shouldBeEqualTo "Biden"
    }

    @Test
    fun `execute statement in coroutines`() = runSuspendWithIO {
        val statement = selectFrom(ACTOR_TABLE_NAME)
            .all()
            .whereColumn("id").eq(bindMarker())
            .limit(10)
            .build()

        val ps = reactiveSession.prepareSuspending(statement)
        val bs = ps.bind().setLong("id", 1212L)

        val rrset = reactiveSession.executeSuspending(bs)
        val rows = rrset.rows().asFlow().toList()
        rows.size shouldBeEqualTo 1
        val row = rows.first()
        row.getString("first_name") shouldBeEqualTo "Joe"
        row.getString("last_name") shouldBeEqualTo "Biden"
    }
}
