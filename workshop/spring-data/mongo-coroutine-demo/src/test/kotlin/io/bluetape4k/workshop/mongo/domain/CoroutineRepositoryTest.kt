package io.bluetape4k.workshop.mongo.domain

import io.bluetape4k.coroutines.flow.toFastList
import io.bluetape4k.junit5.coroutines.runSuspendWithIO
import io.bluetape4k.logging.KLogging
import io.bluetape4k.workshop.mongo.AbstractMongoTest
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldHaveSize
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.ReactiveMongoOperations
import org.springframework.data.mongodb.core.dropCollection

class CoroutineRepositoryTest @Autowired constructor(
    private val repository: PersonCoroutineRepository,
    private val operations: ReactiveMongoOperations,
): AbstractMongoTest() {

    companion object: KLogging()

    @BeforeEach
    fun beforeEach() {
        runBlocking {
            operations.dropCollection<Person>().awaitSingleOrNull()
        }
    }

    @Test
    fun `find one person`() = runSuspendWithIO {
        val person = repository.save(newPerson())

        val loaded = repository.findPersonByFirstname(person.firstname!!)
        loaded.shouldNotBeNull()
        loaded shouldBeEqualTo person
    }

    @Test
    fun `find persons as flow`() = runSuspendWithIO {
        repeat(50) {
            repository.save(newPerson())
        }

        val person1 = repository.save(Person("Sunghyouk", "Bae"))
        val person2 = repository.save(Person("Jehyoung", "Bae"))

        repeat(50) {
            repository.save(newPerson())
        }

        val persons = repository.findAllByLastname("Bae").toFastList()
        persons shouldHaveSize 2
        persons shouldBeEqualTo listOf(person1, person2)

        val sunghyouk = repository.findAllByFirstname("Sunghyouk").toFastList()
        sunghyouk shouldHaveSize 1
        sunghyouk shouldBeEqualTo listOf(person1)
    }
}
