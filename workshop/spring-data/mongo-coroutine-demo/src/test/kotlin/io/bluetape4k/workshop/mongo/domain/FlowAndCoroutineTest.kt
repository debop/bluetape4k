package io.bluetape4k.workshop.mongo.domain

import io.bluetape4k.coroutines.flow.eclipse.toFastList
import io.bluetape4k.junit5.coroutines.runSuspendWithIO
import io.bluetape4k.workshop.mongo.AbstractMongoTest
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.ReactiveMongoOperations
import org.springframework.data.mongodb.core.dropCollection
import org.springframework.data.mongodb.core.find
import org.springframework.data.mongodb.core.findAll
import org.springframework.data.mongodb.core.insert
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.isEqualTo

class FlowAndCoroutineTest(
    @Autowired private val operations: ReactiveMongoOperations,
): AbstractMongoTest() {

    @BeforeEach
    fun beforeEach() {
        runBlocking {
            operations.dropCollection<Person>().awaitSingleOrNull()
        }
    }

    @Test
    fun `find - the coroutine way`() = runSuspendWithIO {
        val person = operations.insert<Person>().one(newPerson()).awaitSingle()

        val query = Query(Criteria.where(Person::firstname.name).isEqualTo(person.firstname!!))
        val loaded = operations.find<Person>(query).awaitSingle()
        loaded shouldBeEqualTo person
    }

    @Test
    fun `find - the flow way`() = runSuspendWithIO {
        val person1 = operations.insert<Person>().one(newPerson()).awaitSingle()
        val person2 = operations.insert<Person>().one(newPerson()).awaitSingle()

        val persons = operations.findAll<Person>().asFlow().toFastList()
        persons shouldBeEqualTo listOf(person1, person2)
    }
}
