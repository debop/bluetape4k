package io.bluetape4k.spring.cassandra.reactive

import com.datastax.oss.driver.api.core.uuid.Uuids
import io.bluetape4k.junit5.coroutines.runSuspendWithIO
import io.bluetape4k.logging.KLogging
import io.bluetape4k.spring.cassandra.AbstractCassandraCoroutineTest
import io.bluetape4k.spring.cassandra.countSuspending
import io.bluetape4k.spring.cassandra.cql.deleteOptions
import io.bluetape4k.spring.cassandra.cql.insertOptions
import io.bluetape4k.spring.cassandra.cql.updateOptions
import io.bluetape4k.spring.cassandra.deleteByIdSuspending
import io.bluetape4k.spring.cassandra.deleteSuspending
import io.bluetape4k.spring.cassandra.domain.DomainTestConfiguration
import io.bluetape4k.spring.cassandra.domain.model.User
import io.bluetape4k.spring.cassandra.existsSuspending
import io.bluetape4k.spring.cassandra.insertSuspending
import io.bluetape4k.spring.cassandra.query.eq
import io.bluetape4k.spring.cassandra.sliceSuspending
import io.bluetape4k.spring.cassandra.truncateSuspending
import io.bluetape4k.spring.cassandra.updateSuspending
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeFalse
import org.amshove.kluent.shouldBeNull
import org.amshove.kluent.shouldBeTrue
import org.amshove.kluent.shouldContainSame
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.cassandra.core.ReactiveCassandraOperations
import org.springframework.data.cassandra.core.UpdateOptions
import org.springframework.data.cassandra.core.query.CassandraPageRequest
import org.springframework.data.cassandra.core.query.Columns
import org.springframework.data.cassandra.core.query.Query
import org.springframework.data.cassandra.core.query.query
import org.springframework.data.cassandra.core.query.where
import org.springframework.data.cassandra.core.selectOneById
import org.springframework.data.cassandra.repository.config.EnableReactiveCassandraRepositories

@SpringBootTest(classes = [DomainTestConfiguration::class])
@EnableReactiveCassandraRepositories
class ReactiveCassandraTemplateTest(
    @Autowired private val operations: ReactiveCassandraOperations,
): AbstractCassandraCoroutineTest("reactive-template") {

    companion object: KLogging() {
        fun newUser(): User {
            return User(Uuids.timeBased().toString(), faker.name().firstName(), faker.name().lastName())
        }
    }

    @BeforeEach
    fun beforeEach() {
        runBlocking {
            operations.truncateSuspending<User>()
        }
    }

    @Test
    fun `context loading`() {
        operations.shouldNotBeNull()
    }

    @Test
    fun `새로운 엔티티 추가`() = runSuspendWithIO {
        val user = newUser()
        operations.insert(user).block()
    }


    @Test
    fun `엔티티가 없을 때에만 추가`() = runSuspendWithIO {
        val lwtOptions = insertOptions { withIfNotExists() }
        val user = newUser()

        val inserted = operations.insertSuspending(user, lwtOptions)
        inserted.wasApplied().shouldBeTrue()
        inserted.entity shouldBeEqualTo user

        val user2 = user.copy(firstname = "성혁")
        val notInserted = operations.insertSuspending(user2, lwtOptions)
        notInserted.wasApplied().shouldBeFalse()
    }

    @Test
    fun `엔티티 Count 조회`() = runSuspendWithIO {
        val user = newUser()

        operations.insertSuspending(user)!!
        operations.countSuspending<User>()!! shouldBeEqualTo 1L
    }

    @Test
    fun `조건절을 이용한 count 조회`() = runSuspendWithIO {
        val user1 = newUser()
        val user2 = newUser()
        operations.insertSuspending(user1)
        operations.insertSuspending(user2)

        operations.countSuspending<User>(query(where("id").eq(user1.id)))!! shouldBeEqualTo 1L
        operations.countSuspending<User>(query(where("id").eq("not-exists")))!! shouldBeEqualTo 0L
    }

    @Test
    fun `exists 조회`() = runSuspendWithIO {
        val user1 = newUser()
        val user2 = newUser()
        operations.insertSuspending(user1)
        operations.insertSuspending(user2)

        operations.existsSuspending<User>(user1.id)!!.shouldBeTrue()
        operations.existsSuspending<User>("not exists id")!!.shouldBeFalse()

        operations.existsSuspending<User>(query(where("id").eq(user1.id)))!!.shouldBeTrue()
        operations.existsSuspending<User>(query(where("id").eq("not-exists@example.com")))!!.shouldBeFalse()
    }

    @Test
    fun `엔티티 갱신하기`() = runSuspendWithIO {
        val user = newUser()
        operations.insertSuspending(user)

        user.firstname = "성혁"
        val updated = operations.updateSuspending<User>(user)!!
        updated.id shouldBeEqualTo user.id
    }

    @Test
    fun `존재하지 않으면 Update 하지 않기`() = runSuspendWithIO {
        // 존재하지 않는 엔티티를 Update 하는 경우에는 아무 작업도 하지 않도록 합니다.
        val user = newUser()
        val lwtOptions = UpdateOptions.builder().withIfExists().build()

        val result = operations.updateSuspending(user, lwtOptions)

        result.wasApplied().shouldBeFalse()
        getUserById(user.id).shouldBeNull()
    }

    @Test
    fun `존재하는 엔티티를 Update 하기`() = runSuspendWithIO {
        val user = newUser()
        operations.insertSuspending(user)!!

        user.firstname = "성혁"
        val lwtOptions = updateOptions { withIfExists() }
        val result = operations.updateSuspending(user, lwtOptions)
        result.wasApplied().shouldBeTrue()
        getUserById(user.id) shouldBeEqualTo user
    }

    @Test
    fun `조건절로 엔티티 삭제하기`() = runSuspendWithIO {
        val user = newUser()
        operations.insertSuspending(user)!!

        val query = query(where("id").eq(user.id))
        operations.deleteSuspending<User>(query)!!.shouldBeTrue()

        getUserById(user.id).shouldBeNull()
    }

    @Test
    fun `특정 컬럼만 삭제하기`() = runSuspendWithIO {
        val user = newUser()
        operations.insertSuspending(user)!!

        val query = query(where("id").eq(user.id))
            .columns(Columns.from("lastname"))

        operations.deleteSuspending<User>(query)!!.shouldBeTrue()

        val loaded = getUserById(user.id)!!
        loaded.firstname shouldBeEqualTo user.firstname
        loaded.lastname.shouldBeNull()
    }

    @Test
    fun `엔티티 삭제하기`() = runSuspendWithIO {
        val user = newUser()
        operations.insertSuspending(user)!!

        operations.deleteSuspending(user)

        getUserById(user.id).shouldBeNull()
    }

    @Test
    fun `Id로 엔티티 삭제하기`() = runSuspendWithIO {
        val user = newUser()
        operations.insertSuspending(user)!!

        operations.deleteByIdSuspending<User>(user.id)!!.shouldBeTrue()
        getUserById(user.id).shouldBeNull()
    }

    @Test
    fun `존재하는 엔티티만 삭제하기`() = runSuspendWithIO {
        val user = newUser()
        operations.insertSuspending(user)!!

        val lwtOptions = deleteOptions { withIfExists() }
        operations.deleteSuspending(user, lwtOptions).wasApplied().shouldBeTrue()
        getUserById(user.id).shouldBeNull()

        // 이미 삭제되었으므로, 재삭제 요청은 처리되지 않습니다.
        operations.deleteSuspending(user, lwtOptions).wasApplied().shouldBeFalse()
    }

    @Test
    fun `조건절에 queryOptions 적용하여 삭제하기`() = runSuspendWithIO {
        val user = newUser()
        operations.insertSuspending(user)!!

        val lwtOptions = deleteOptions { withIfExists() }
        val query = query(where("id").eq(user.id)).queryOptions(lwtOptions)

        operations.deleteSuspending<User>(query)!!.shouldBeTrue()
        getUserById(user.id).shouldBeNull()

        // 이미 삭제되었으므로, 재삭제 요청은 처리되지 않습니다.
        operations.deleteSuspending<User>(query)!!.shouldBeFalse()
    }

    @Test
    fun `PageRequest를 이용하여 Slice로 조회`() = runSuspendWithIO {
        val entitySize = 100
        val expectedIds = mutableSetOf<String>()

        val jobs = List(entitySize) { index ->
            launch {
                val user = User("debop-$index", "Debop", "Bae")
                expectedIds.add(user.id)
                operations.insert(user).awaitSingle()
            }
        }
        jobs.joinAll()

        val query = Query.empty()
        var slice = operations.sliceSuspending<User>(query.pageRequest(CassandraPageRequest.first(10)))

        val loadIds = mutableSetOf<String>()
        var iterations = 0

        do {
            iterations++

            slice.size shouldBeEqualTo 10
            loadIds.addAll(slice.map { it.id })

            if (slice.hasNext()) {
                slice = operations.sliceSuspending<User>(query.pageRequest(slice.nextPageable()))
            } else {
                break
            }
        } while (slice.content.isNotEmpty())

        loadIds.size shouldBeEqualTo entitySize
        loadIds shouldContainSame expectedIds
        iterations shouldBeEqualTo entitySize / 10
    }

    private suspend fun getUserById(userId: String): User? =
        operations.selectOneById<User>(userId).awaitSingleOrNull()
}
