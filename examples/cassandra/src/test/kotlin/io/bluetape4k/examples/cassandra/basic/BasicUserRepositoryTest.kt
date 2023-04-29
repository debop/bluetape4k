package io.bluetape4k.examples.cassandra.basic

import com.datastax.oss.driver.api.querybuilder.SchemaBuilder
import io.bluetape4k.data.cassandra.cql.executeSuspending
import io.bluetape4k.examples.cassandra.AbstractCassandraCoroutineTest
import io.bluetape4k.junit5.coroutines.runSuspendWithIO
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeNull
import org.amshove.kluent.shouldBeTrue
import org.amshove.kluent.shouldContain
import org.amshove.kluent.shouldNotBeEmpty
import org.junit.jupiter.api.Assumptions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(classes = [BasicConfiguration::class])
class BasicUserRepositoryTest(
    @Autowired private val repository: BasicUserRepository,
): AbstractCassandraCoroutineTest("basic-user") {

    companion object: KLogging()

    private fun newBasicUser(): BasicUser {
        return BasicUser(
            faker.random().nextLong(10000, 100000),
            faker.name().username(),
            faker.name().firstName(),
            faker.name().lastName()
        )
    }

    @BeforeEach
    fun beforeEach() {
        runBlocking {
            repository.deleteAll()
        }
    }

    @Test
    fun `find saved user by id`() = runSuspendWithIO {
        val user = newBasicUser()
        repository.save(user)
        repository.findById(user.id) shouldBeEqualTo user
    }

    @Test
    fun `find by annotated query method`() = runSuspendWithIO {
        val user = newBasicUser()
        repository.save(user)

        repository.findUserByIdIn(10).shouldBeNull()
        repository.findUserByIdIn(user.id) shouldBeEqualTo user
    }

    @Test
    fun `find by drived query method`() = runSuspendWithIO {
        val createIndexStmt = SchemaBuilder
            .createIndex("basic_users_uname")
            .ifNotExists()
            .onTable("basic_users")
            .andColumn("uname")
            .build()
        log.debug { "Create Index=${createIndexStmt.query}" }

        session.executeSuspending(createIndexStmt).wasApplied().shouldBeTrue()

        // session.executeSuspend("CREATE INDEX IF NOT EXISTS basic_users_uname ON basic_users (uname);")

        /*
		  Cassandra secondary indexes are created in the background without the possibility to check
		  whether they are available or not. So we are forced to just wait. *sigh*
		 */
        log.debug { "Wait for create index ... 'basic_users_uname' " }
        delay(100)

        val user = newBasicUser()
        repository.save(user)

        repository.findByUsername(user.username) shouldBeEqualTo user
    }

    /**
     * Cassandra release version이 3.4 이상에서 지원하는 기능입니다.
     */
    @Test
    fun `find by derived query method with SASI`() = runSuspendWithIO {
        // NOTE: SASI indexes are disabled. Enable in cassandra.yaml to use.
        Assumptions.assumeTrue { false }
        session.executeSuspending(
            "CREATE CUSTOM INDEX ON basic_users (lname) USING 'org.apache.cassandra.index.sasi.SASIIndex';"
        )

        /*
            Cassandra secondary indexes are created in the background without the possibility to check
            whether they are available or not. So we are forced to just wait. *sigh*
        */
        delay(1000)

        val user = newBasicUser()
        repository.save(user)

        repository.findAllByLastnameStartsWith(user.lastname) shouldBeEqualTo user
    }

    @Disabled("SASI Index 는 Cassandra Server 환경설정에서 enable 해야 합니다.")
    @Test
    fun `find multiple rows with allow filtering`() = runSuspendWithIO {
        val users = List(6) {
            newBasicUser().copy(id = it + 1L)
        }
        repository.saveAll(users.asFlow()).collect()

        val loaded = repository.findAllByLastnameStartsWith(users[2].lastname.substring(0, 2)).toList()
        loaded.shouldNotBeEmpty()
        loaded shouldContain users[2]
    }
}
