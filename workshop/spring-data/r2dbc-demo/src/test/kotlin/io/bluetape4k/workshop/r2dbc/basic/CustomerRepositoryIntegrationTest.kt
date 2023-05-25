package io.bluetape4k.workshop.r2dbc.basic

import io.bluetape4k.logging.KLogging
import io.bluetape4k.workshop.r2dbc.basics.Customer
import io.bluetape4k.workshop.r2dbc.basics.CustomerRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.r2dbc.core.DatabaseClient

@SpringBootTest(classes = [InfrastructureConfiguration::class])
class CustomerRepositoryIntegrationTest(
    @Autowired private val customerRepo: CustomerRepository,
    @Autowired private val database: DatabaseClient,
) {

    companion object: KLogging()

    @BeforeEach
    fun beforeEach() {
        runBlocking {
            val statements = listOf(
                "DROP TABLE IF EXISTS customer;",
                "CREATE TABLE customer (id SERIAL PRIMARY KEY, firstname VARCHAR(100) NOT NULL, lastname VARCHAR(100) NOT NULL);",
            )

            statements.forEach {
                database.sql(it)
                    .fetch()
                    .rowsUpdated()
                    .awaitSingle()
            }
        }
    }

    @Test
    fun `context loading`() {
        database.shouldNotBeNull()
    }

    @Test
    fun `execute find all`() = runTest {
        val dave = Customer("Dave", "Matthews")
        val carter = Customer("Carter", "Beauford")

        insertCustomers(dave, carter)

        val customers = customerRepo.findAll().toList()

        customers shouldBeEqualTo listOf(dave, carter)
    }

    @Test
    fun `execute annotated query`() = runTest {
        val dave = Customer("Dave", "Matthews")
        val carter = Customer("Carter", "Beauford")

        insertCustomers(dave, carter)

        val customer = customerRepo.findByLastname("Matthews").first()
        customer.shouldNotBeNull() shouldBeEqualTo dave
    }


    private suspend fun insertCustomers(vararg customers: Customer) {
        customerRepo.saveAll(customers.toList()).collect()
    }
}
