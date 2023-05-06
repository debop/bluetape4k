package io.bluetape4k.examples.cassandra.streamnullable

import io.bluetape4k.examples.cassandra.AbstractCassandraTest
import io.bluetape4k.logging.KLogging
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeNull
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(classes = [StreamNullableTestConfiguration::class])
class StreamNullableTest(
    @Autowired private val repository: PersonRepository,
): AbstractCassandraTest() {

    companion object: KLogging()

    @BeforeEach
    fun setup() {
        runCatching { repository.deleteAll() }
    }

    @Test
    fun `provide find one with nullable`() {
        val homer = repository.save(Person("1", "Homer", "Simpson"))

        repository.findById("1").shouldNotBeNull()
        repository.findById(homer.id + 1).shouldBeNull()
    }

    @Test
    fun `invoke default function`() {
        val homer = repository.save(Person("1", "Homer", "Simpson"))
        val loaded = repository.findByPerson(homer)

        loaded shouldBeEqualTo homer
    }

    @Test
    fun `use Java8 Stream with custom query`() {
        val homer = repository.save(Person("1", "Homer", "Simpson"))
        val bart = repository.save(Person("2", "Bart", "Simpson"))

        val stream = repository.findAll()
        stream.toList().sorted() shouldBeEqualTo listOf(homer, bart)
    }

}
