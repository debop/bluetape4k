package io.bluetape4k.workshop.r2dbc.queryexample

import io.bluetape4k.logging.KLogging
import io.bluetape4k.spring.data.buildExampleMatcher
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldContainSame
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.Example
import org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers
import org.springframework.data.domain.ExampleMatcher.matching
import org.springframework.r2dbc.core.DatabaseClient
import java.util.*

@SpringBootTest(classes = [InfrastructureConfiguration::class])
class PersonRepositoryIntegrationTest @Autowired constructor(
    private val repository: PersonRepository,
    private val client: DatabaseClient,
) {

    companion object: KLogging()

    private lateinit var skyler: Person
    private lateinit var walter: Person
    private lateinit var flynn: Person
    private lateinit var marie: Person
    private lateinit var hank: Person

    @BeforeEach
    fun beforeEach() {
        runBlocking {
            val statements = listOf(
                "DROP TABLE IF EXISTS person;",
                """
                CREATE TABLE person (
                    id SERIAL PRIMARY KEY, 
                    firstname VARCHAR(100) NOT NULL, 
                    lastname VARCHAR(100) NOT NULL, 
                    age INTEGER NOT NULL
                );""".trimIndent()
            )

            skyler = Person("Skyler", "White", 45)
            walter = Person("Walter", "White", 50)
            flynn = Person("Walter Jr. (Flynn)", "White", 17)
            marie = Person("Marie", "Schrader", 38)
            hank = Person("Hank", "Schrader", 43)

            statements.forEach {
                client.sql(it).fetch().rowsUpdated().awaitSingleOrNull()
            }

            repository.saveAll(listOf(skyler, walter, flynn, marie, hank)).collect()
        }
    }

    @Test
    fun `context loading`() {
        client.shouldNotBeNull()
        repository.shouldNotBeNull()
    }

    @Test
    fun `count by simple example`() = runTest {

        // Kotlin 클래스에 대해서 non-null 때문에 Example 만드는 것을 이렇게 Example에 지정할 속성명을 특정해주는 [ExampleMatcher]를 사용해야 한다!!!
        val matcher = Person::class
            .buildExampleMatcher(Person::lastname.name)
            .withMatcher(Person::lastname.name, GenericPropertyMatchers.exact())
            .withIgnoreNullValues()

        val personExample = Person("", "White", 0)
        val example = Example.of(personExample, matcher)

        repository.count(example).awaitSingle() shouldBeEqualTo 3L
    }

    @Test
    fun `ignore properties and match by age`() = runTest {
        // Kotlin 클래스에 대해서 non-null 때문에 Example 만드는 것을 이렇게 Example에 지정할 속성명을 특정해주는 [ExampleMatcher]를 사용해야 한다!!!
        val matcher = Person::class
            .buildExampleMatcher(Person::age.name)
            .withMatcher(Person::age.name, GenericPropertyMatchers.exact())
            .withIgnoreNullValues()

        val example = Example.of(flynn, matcher)

        repository.findOne(example).awaitSingleOrNull() shouldBeEqualTo flynn
    }

    @Test
    fun `match starting strings ignore case`() = runTest {
        // Kotlin 클래스에 대해서 non-null 때문에 Example 만드는 것을 이렇게 Example에 지정할 속성명을 특정해주는 [ExampleMatcher]를 사용해야 한다!!!
        val matcher = Person::class
            .buildExampleMatcher(Person::firstname.name, Person::lastname.name)
            .withMatcher(Person::firstname.name, GenericPropertyMatchers.startsWith())
            .withMatcher(Person::lastname.name, GenericPropertyMatchers.ignoreCase())
            .withIgnoreNullValues()

        val example = Example.of(Person("Walter", "WHITE", 0), matcher)

        repository.findAll(example).asFlow().toList() shouldContainSame listOf(walter, flynn)
    }

    @Test
    fun `configuring matchers using lambdas`() = runTest {
        // Kotlin 클래스에 대해서 non-null 때문에 Example 만드는 것을 이렇게 Example에 지정할 속성명을 특정해주는 [ExampleMatcher]를 사용해야 한다!!!
        val matcher = matching()
            .withIgnorePaths(Person::age.name)
            .withMatcher(Person::firstname.name, GenericPropertyMatchers.startsWith())
            .withMatcher(Person::lastname.name, GenericPropertyMatchers.ignoreCase())
            .withIgnoreNullValues()

        val example = Example.of(Person("Walter", "WHITE", 0), matcher)

        repository.findAll(example).asFlow().toList() shouldContainSame listOf(walter, flynn)
    }

    @Test
    fun `value transformer`() = runTest {
        // Kotlin 클래스에 대해서 non-null 때문에 Example 만드는 것을 이렇게 Example에 지정할 속성명을 특정해주는 [ExampleMatcher]를 사용해야 한다!!!
        val matcher = Person::class
            .buildExampleMatcher(Person::lastname.name, Person::age.name)
            .withMatcher(Person::age.name) { it.transform { Optional.of(50) } }
            .withIgnoreNullValues()

        val example = Example.of(Person("", "White", 99), matcher)

        repository.findOne(example).awaitSingleOrNull() shouldBeEqualTo walter
    }
}
