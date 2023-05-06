package io.bluetape4k.spring.cassandra.reactive

import com.datastax.oss.driver.api.core.uuid.Uuids
import io.bluetape4k.junit5.coroutines.runSuspendTest
import io.bluetape4k.logging.KLogging
import io.bluetape4k.spring.cassandra.AbstractCassandraCoroutineTest
import io.bluetape4k.spring.cassandra.AbstractReactiveCassandraTestConfiguration
import io.bluetape4k.spring.cassandra.countSuspending
import io.bluetape4k.spring.cassandra.insertSuspending
import io.bluetape4k.spring.cassandra.query.eq
import io.bluetape4k.spring.cassandra.selectForFlow
import io.bluetape4k.spring.cassandra.truncateSuspending
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldBeEmpty
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Configuration
import org.springframework.data.annotation.Id
import org.springframework.data.cassandra.core.ReactiveCassandraOperations
import org.springframework.data.cassandra.core.delete
import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.Indexed
import org.springframework.data.cassandra.core.mapping.Table
import org.springframework.data.cassandra.core.query.Query
import org.springframework.data.cassandra.core.query.inValues
import org.springframework.data.cassandra.core.query.query
import org.springframework.data.cassandra.core.query.where
import java.io.Serializable

@SpringBootTest
class ReactiveDeleteOperationsTest(
    @Autowired private val operations: ReactiveCassandraOperations,
): AbstractCassandraCoroutineTest("delete-op") {

    companion object: KLogging() {
        private const val PERSON_TABLE_NAME = "delete_op_person"
    }

    @Configuration
    //@EntityScan(basePackageClasses = [Person::class]) // 내부 엔티티는 Scan 없이도 사용 가능하다
    class TestConfiguration: AbstractReactiveCassandraTestConfiguration()

    @Table(PERSON_TABLE_NAME)
    private data class Person(
        @field:Id val id: String,
        @field:Indexed var firstName: String,
        @field:Indexed var lastName: String,
    ): Serializable

    private fun newPerson(): Person {
        return Person(
            id = Uuids.timeBased().toString(),
            firstName = faker.name().firstName(),
            lastName = faker.name().lastName()
        )
    }

    private data class Jedi(
        @field:Column("firstname") val name: String,
    )

    private val han = newPerson()
    private val luke = newPerson()

    @BeforeEach
    fun beforeEach() {
        runBlocking {
            operations.truncateSuspending<Person>()

            operations.insertSuspending(han)
            operations.insert(luke).awaitSingle()
        }
    }


    @Test
    fun `matching 을 이용하여 엔티티 삭제하기`() = runSuspendTest {
        val writeResult = operations
            .delete<Person>()
            .matching(query(where("id").eq(han.id)))
            .all()
            .awaitSingle()

        writeResult.wasApplied().shouldBeTrue()
    }

    @Test
    fun `matching 되는 대체 타입 중 컬럼을 삭제하기`() = runSuspendTest {
        val writeResult = operations
            .delete<Jedi>().inTable(PERSON_TABLE_NAME)
            .matching(query(where("id").inValues(han.id, luke.id)))
            .all()
            .awaitSingle()

        writeResult.wasApplied().shouldBeTrue()

        operations.countSuspending<Person>() shouldBeEqualTo 0L
        operations.selectForFlow<Person>(Query.empty()).toList().shouldBeEmpty()
    }
}
