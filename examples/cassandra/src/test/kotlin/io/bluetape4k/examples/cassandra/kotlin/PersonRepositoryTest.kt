package io.bluetape4k.examples.cassandra.kotlin

import io.bluetape4k.examples.cassandra.AbstractCassandraCoroutineTest
import io.bluetape4k.junit5.coroutines.runSuspendWithIO
import io.bluetape4k.logging.KLogging
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeNull
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.cassandra.core.CassandraOperations

@SpringBootTest(classes = [PersonTestConfiguration::class])
class PersonRepositoryTest(
    @Autowired private val operations: CassandraOperations,
    @Autowired private val repository: PersonRepository,
): AbstractCassandraCoroutineTest("kotlin-person-repo") {

    companion object: KLogging()


    @BeforeEach
    fun beforeEach() {
        runSuspendWithIO {
            insertPeople()
        }
    }

    @Test
    fun `context loading`() {
        operations.shouldNotBeNull()
        repository.shouldNotBeNull()
    }

    @Test
    fun `insert and load kotlin data class`() = runSuspendWithIO {
        val debop = repository.findOneByFirstname("Debop")
        debop shouldBeEqualTo Person("Debop", "Bae")
    }

    @Test
    fun `return null if no person found`() = runSuspendWithIO {
        repository.findOneOrNoneByFirstname("Debop").shouldNotBeNull()
        repository.findOneOrNoneByFirstname("Han").shouldNotBeNull()

        repository.findOneOrNoneByFirstname("Not exists").shouldBeNull()
    }

    @Test
    fun `find nullalbe`() = runSuspendWithIO {
        repository.findNullableByFirstname("Debop").shouldNotBeNull()
        repository.findNullableByFirstname("Han").shouldNotBeNull()

        repository.findNullableByFirstname("Not exists").shouldBeNull()
    }

    @Test
    fun `레코드가 없는 경우 coroutines 은 null을 반환한다`() = runSuspendWithIO {
        repository.findOneByFirstname("Not exists").shouldBeNull()
    }

    private fun newPerson(): Person {
        return Person(faker.name().firstName(), faker.name().lastName())
    }

    private suspend fun insertPeople() {
        repository.deleteAll()

        val users = flowOf(
            Person("Debop", "Bae"),
            Person("Han", "Solo"),
            newPerson(),
            newPerson(),
            newPerson(),
            newPerson(),
        )
        repository.saveAll(users).collect()
    }
}
