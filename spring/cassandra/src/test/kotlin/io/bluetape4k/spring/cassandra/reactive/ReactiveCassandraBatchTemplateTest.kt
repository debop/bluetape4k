package io.bluetape4k.spring.cassandra.reactive

import io.bluetape4k.junit5.coroutines.runSuspendWithIO
import io.bluetape4k.spring.cassandra.AbstractCassandraCoroutineTest
import io.bluetape4k.spring.cassandra.cql.insertOptions
import io.bluetape4k.spring.cassandra.cql.queryForResultSetSuspending
import io.bluetape4k.spring.cassandra.cql.writeOptions
import io.bluetape4k.spring.cassandra.domain.DomainTestConfiguration
import io.bluetape4k.spring.cassandra.domain.model.FlatGroup
import io.bluetape4k.spring.cassandra.domain.model.Group
import io.bluetape4k.spring.cassandra.domain.model.GroupKey
import io.bluetape4k.spring.cassandra.executeSuspend
import io.bluetape4k.spring.cassandra.insertFlow
import io.bluetape4k.spring.cassandra.insertSuspending
import io.bluetape4k.spring.cassandra.selectOneByIdSuspending
import io.bluetape4k.spring.cassandra.truncateSuspending
import io.bluetape4k.spring.cassandra.updateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeFalse
import org.amshove.kluent.shouldBeInRange
import org.amshove.kluent.shouldBeNull
import org.amshove.kluent.shouldNotBeEmpty
import org.amshove.kluent.shouldNotBeEqualTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.cassandra.ReactiveResultSet
import org.springframework.data.cassandra.core.InsertOptions
import org.springframework.data.cassandra.core.ReactiveCassandraOperations
import org.springframework.data.cassandra.core.cql.WriteOptions
import org.springframework.data.cassandra.repository.config.EnableReactiveCassandraRepositories
import java.util.concurrent.TimeUnit
import kotlin.test.assertFailsWith

@SpringBootTest(classes = [DomainTestConfiguration::class])
@EnableReactiveCassandraRepositories
class ReactiveCassandraBatchTemplateTest(
    @Autowired private val operations: ReactiveCassandraOperations,
): AbstractCassandraCoroutineTest("reactive-batch-template") {

    private val group1 = Group(GroupKey("users", "0x01", faker.name().username()))
    private val group2 = Group(GroupKey("users", "0x01", faker.name().username()))

    private fun newGroup(): Group {
        return Group(
            GroupKey(
                faker.internet().domainName(),
                faker.internet().domainWord(),
                faker.name().username()
            )
        ).apply {
            email = faker.internet().emailAddress()
            age = faker.random().nextInt(10, 80)
        }
    }

    private fun newFlatGroup(): FlatGroup {
        return FlatGroup(
            faker.internet().domainName(),
            faker.internet().domainWord(),
            faker.name().username()
        ).apply {
            email = faker.internet().emailAddress()
            age = faker.random().nextInt(10, 80)
        }
    }

    @BeforeEach
    fun beforeEach() {
        runBlocking {
            operations.truncateSuspending<Group>()
            operations.truncateSuspending<FlatGroup>()

            operations.insertSuspending(group1)
            operations.insertSuspending(group2)
        }
    }

    @Test
    fun `insert entities in batch ops`() = runSuspendWithIO {
        val g1 = newGroup()
        val g2 = newGroup()

        operations.batchOps()
            .insert(g1)
            .insert(g2)
            .executeSuspend()

        val loaded = operations.selectOneByIdSuspending<Group>(g1.id)
        loaded shouldBeEqualTo g1
    }


    @Test
    fun `insert vararg entities reject query options`() = runSuspendWithIO {
        // entity 만 가능합니다. 
        assertFailsWith<IllegalArgumentException> {
            operations.batchOps().insert(group1, group2, InsertOptions.empty())
        }
    }

    @Test
    fun `insert entities with LWT`() = runSuspendWithIO {
        val lwtOptions = insertOptions { withIfNotExists() }

        val prevGroup1 = group1.copy().apply { age = 54 }
        operations.insertSuspending(prevGroup1)

        group1.age = 100

        val writeResult = operations.batchOps().insert(group1, lwtOptions).insert(group2).executeSuspend()
        writeResult.wasApplied().shouldBeFalse()
        writeResult.executionInfo.isNotEmpty()
        writeResult.rows.shouldNotBeEmpty()

        val loadedDebop = operations.selectOneByIdSuspending<Group>(group1.id)!!
        loadedDebop shouldBeEqualTo prevGroup1
        loadedDebop.age shouldNotBeEqualTo group1.age

        val loadedMike = operations.selectOneByIdSuspending<Group>(group2.id)!!
        loadedMike shouldBeEqualTo group2
    }

    @Test
    fun `insert entity flow with ttl`() = runSuspendWithIO {
        group1.email = "debop@example.com"
        group2.email = "mike@example.com"

        val writeOptions = WriteOptions.builder().ttl(30).build()

        operations.batchOps().insertFlow(flowOf(group1, group2), writeOptions).executeSuspend()

        val resultSet: ReactiveResultSet = operations.reactiveCqlOperations
            .queryForResultSetSuspending("SELECT TTL(email) FROM groups")

        resultSet.rows().asFlow()
            .onEach { row -> println("ttl= ${row.getInt(0)}") }
            .onEach { row ->
                row.getInt(0) shouldBeInRange 1..30
            }
            .collect()
    }

    @Test
    fun `update vararg entities should reject query options`() = runSuspendWithIO {
        assertFailsWith<IllegalArgumentException> {
            operations.batchOps().update(group1, group2, InsertOptions.empty())
        }
    }

    @Test
    fun `update entities`() = runSuspendWithIO {
        group1.email = "debop@example.com"
        group2.email = "mike@example.com"

        operations.batchOps().update(group1).update(group2).executeSuspend()

        val loaded = operations.selectOneByIdSuspending<Group>(group1.id)!!
        loaded.email shouldBeEqualTo group1.email
    }

    @Test
    fun `update entity flow`() = runSuspendWithIO {
        group1.email = "debop@example.com"
        group2.email = "mike@example.com"

        operations.batchOps().updateFlow(flowOf(group1, group2)).executeSuspend()

        val loaded = operations.selectOneByIdSuspending<Group>(group1.id)!!
        loaded.email shouldBeEqualTo group1.email
    }

    @Test
    fun `update entity with ttl`() = runSuspendWithIO {
        group1.email = "debop@example.com"
        group2.email = "mike@example.com"

        val writeOptions = writeOptions { ttl(30) }

        operations.batchOps().update(group1, writeOptions).executeSuspend()

        val resultSet: ReactiveResultSet =
            operations.reactiveCqlOperations.queryForResultSetSuspending("SELECT TTL(email), email FROM groups")

        resultSet.rows().asFlow()
            .onEach { row -> println("ttl= ${row.getInt(0)}") }
            .onEach { row ->
                if (row.getString("email") == group1.email) {
                    row.getInt(0) shouldBeInRange 1..30
                } else {
                    row.getInt(0) shouldBeEqualTo 0
                }
            }
            .collect()
    }

    @Test
    fun `update flow of entities`() = runSuspendWithIO {
        val flatGroup1 = newFlatGroup()
        val flatGroup2 = newFlatGroup()

        operations.insertSuspending(flatGroup1)
        operations.insertSuspending(flatGroup2)

        flatGroup1.email = faker.internet().emailAddress()
        flatGroup2.email = faker.internet().emailAddress()

        operations.batchOps().updateFlow(flowOf(flatGroup1, flatGroup2)).executeSuspend()

        val loaded = operations.selectOneByIdSuspending<FlatGroup>(flatGroup1)!!
        loaded.email shouldBeEqualTo flatGroup1.email
    }

    @Test
    fun `delete as vararg reject query options`() = runSuspendWithIO {
        assertFailsWith<IllegalArgumentException> {
            operations.batchOps().delete(group1, group2, InsertOptions.empty())
        }
    }

    @Test
    fun `deelete entities`() = runSuspendWithIO {
        operations.batchOps().delete(group1).delete(group2).executeSuspend()

        val loaded = operations.selectOneByIdSuspending<Group>(group1.id)
        loaded.shouldBeNull()
    }

    @Test
    fun `apply timestamp to all entities`() = runSuspendWithIO {
        group1.email = faker.internet().emailAddress()
        group2.email = faker.internet().emailAddress()

        // timestamp 는 nano seconds 단위이다.
        val timestamp = (System.currentTimeMillis() + TimeUnit.DAYS.toMillis(1)) * 1000

        operations.batchOps()
            .insert(group1)
            .insert(group2)
            .withTimestamp(timestamp)
            .executeSuspend()

        val resultSet: ReactiveResultSet =
            operations.reactiveCqlOperations.queryForResultSetSuspending("SELECT writetime(email) FROM groups")

        resultSet.rows().asFlow()
            .onEach { row -> println("timestamp= ${row.getLong(0)}") }
            .onEach { row ->
                row.getLong(0) shouldBeEqualTo timestamp
            }
            .collect()
    }

    @Test
    fun `batchOps 는 중복 실행이 안됩니다`() = runSuspendWithIO {
        val batchOps = operations.batchOps()
        batchOps.insert(group1).executeSuspend()

        assertFailsWith<IllegalStateException> {
            batchOps.executeSuspend()
        }
    }

    @Test
    fun `batchOps 는 실행 후 변경은 허용되지 않습니다`() = runSuspendWithIO {
        val batchOps = operations.batchOps()
        batchOps.insert(group1).executeSuspend()

        assertFailsWith<IllegalStateException> {
            batchOps.update(Group())
        }
    }
}
