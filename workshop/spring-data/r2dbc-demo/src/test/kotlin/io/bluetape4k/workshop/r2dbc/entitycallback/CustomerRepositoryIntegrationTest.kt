package io.bluetape4k.workshop.r2dbc.entitycallback

import io.bluetape4k.logging.KLogging
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeFalse
import org.amshove.kluent.shouldBeTrue
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.r2dbc.core.DatabaseClient

@SpringBootTest(classes = [ApplicationConfiguration::class])
class CustomerRepositoryIntegrationTest(
    @Autowired private val repository: CustomerRepository,
    @Autowired private val database: DatabaseClient,
) {

    companion object: KLogging()

    @Test
    fun `context loading`() {
        repository.shouldNotBeNull()
        database.shouldNotBeNull()
    }

    @Test
    fun `generates id on insert`() = runTest {
        val dave = Customer("Dave", "Matthews")

        val saved = repository.save(dave)

        dave.hasId.shouldBeFalse()
        saved.hasId.shouldBeTrue()
    }
}
