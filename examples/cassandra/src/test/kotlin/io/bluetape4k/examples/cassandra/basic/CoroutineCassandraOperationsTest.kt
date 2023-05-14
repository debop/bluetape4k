package io.bluetape4k.examples.cassandra.basic

import com.datastax.oss.driver.api.core.CqlSession
import com.datastax.oss.driver.api.core.cql.Row
import com.datastax.oss.driver.api.querybuilder.QueryBuilder
import com.datastax.oss.driver.api.querybuilder.QueryBuilder.insertInto
import com.datastax.oss.driver.api.querybuilder.QueryBuilder.selectFrom
import io.bluetape4k.collections.eclipse.fastList
import io.bluetape4k.data.cassandra.querybuilder.literal
import io.bluetape4k.examples.cassandra.AbstractCassandraCoroutineTest
import io.bluetape4k.junit5.coroutines.runSuspendWithIO
import io.bluetape4k.logging.KLogging
import io.bluetape4k.spring.cassandra.executeSuspending
import io.bluetape4k.spring.cassandra.insertSuspending
import io.bluetape4k.spring.cassandra.selectOneByIdSuspending
import io.bluetape4k.spring.cassandra.selectOneSuspending
import io.bluetape4k.spring.cassandra.selectSuspending
import io.bluetape4k.spring.cassandra.updateSuspending
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.cassandra.core.AsyncCassandraOperations
import org.springframework.data.cassandra.core.AsyncCassandraTemplate

@SpringBootTest(classes = [BasicConfiguration::class])
class CoroutineCassandraOperationsTest(
    @Autowired private val cqlSession: CqlSession,
): AbstractCassandraCoroutineTest("basic-user-ops") {

    companion object: KLogging() {
        private const val USER_TABLE = "basic_users"
    }

    // NOTE: AsyncCassandraTemplate 는 직접 Injection 받을 수 없고, 이렇게 생성해야 한다.
    private val operations: AsyncCassandraOperations by lazy { AsyncCassandraTemplate(cqlSession) }

    @BeforeEach
    fun setup() {
        runBlocking(Dispatchers.IO) {
            operations.executeSuspending(QueryBuilder.truncate(USER_TABLE).build())
        }
    }

    @Test
    fun `insert and select in coroutines`() = runSuspendWithIO {
        val insertStmt = insertInto(USER_TABLE)
            .value("user_id", 42L.literal())
            .value("uname", "debop".literal())
            .value("fname", "Debop".literal())
            .value("lname", "Bae".literal())
            .ifNotExists()
            .build()

        operations.executeSuspending(insertStmt)

        val user = operations.selectOneByIdSuspending<BasicUser>(42L)!!
        user.username shouldBeEqualTo "debop"

        val users = operations.selectSuspending<BasicUser>(selectFrom(USER_TABLE).all().build())
        users shouldBeEqualTo listOf(user)
    }

    @Test
    fun `insert and update`() = runSuspendWithIO {
        val user = BasicUser(42L, faker.name().username(), faker.name().firstName(), faker.name().lastName())
        operations.insertSuspending(user)

        val updated = user.copy(firstname = faker.name().firstName())
        operations.updateSuspending(updated)

        val loaded = operations.selectOneByIdSuspending<BasicUser>(user.id)!!
        loaded shouldBeEqualTo updated
    }

    @Test
    fun `insert in coroutines`() = runSuspendWithIO {
        val users = fastList(100) {
            BasicUser(
                it.toLong(),
                "uname-$it",
                "firstname-$it",
                "lastname-$it"
            )
        }

        val tasks = users.map {
            async(Dispatchers.IO) {
                operations.insertSuspending(it)
            }
        }
        tasks.awaitAll()
    }

    @Test
    fun `select async projections`() = runSuspendWithIO {
        val user = BasicUser(42L, faker.name().username(), faker.name().firstName(), faker.name().lastName())
        operations.insertSuspending(user)

        val id = operations.selectOneSuspending<Long>(selectFrom(USER_TABLE).column("user_id").build())!!
        id.shouldNotBeNull().shouldBeEqualTo(user.id)

        val row = operations.selectOneSuspending<Row>(selectFrom(USER_TABLE).column("user_id").asCql())
        row.shouldNotBeNull()
        row.getLong(0) shouldBeEqualTo user.id

        val map = operations.selectOneSuspending<Map<*, *>>(selectFrom(USER_TABLE).all().limit(1).asCql())

        map.shouldNotBeNull()
        map["user_id"] shouldBeEqualTo user.id
        map["uname"] shouldBeEqualTo user.username
        map["fname"] shouldBeEqualTo user.firstname
        map["lname"] shouldBeEqualTo user.lastname
    }
}
