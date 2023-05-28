package io.bluetape4k.workshop.r2dbc.basic

import io.bluetape4k.logging.KLogging
import io.bluetape4k.workshop.r2dbc.basics.Customer
import io.bluetape4k.workshop.r2dbc.basics.CustomerRepository
import io.bluetape4k.workshop.r2dbc.basics.TransactionalService
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeNull
import org.amshove.kluent.shouldBeTrue
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.r2dbc.core.DatabaseClient
import kotlin.test.assertFailsWith

@SpringBootTest(classes = [InfrastructureConfiguration::class])
class TransactionalServiceIntegrationTest @Autowired constructor(
    private val service: TransactionalService,
    private val repository: CustomerRepository,
    private val database: DatabaseClient,
) {

    companion object: KLogging()

    @BeforeEach
    fun beforeEach() {
        runBlocking {
            val statements = listOf(
                "DROP TABLE IF EXISTS customer;",
                """
                CREATE TABLE customer (
                    id SERIAL PRIMARY KEY, 
                    firstname VARCHAR(100) NOT NULL, 
                    lastname VARCHAR(100) NOT NULL
                );
                """.trimIndent(),
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
    fun `exception triggers rollback`() = runTest {

        // Dave 저장 시 예외가 발생하여 Rollback 하게 된다.
        assertFailsWith<IllegalStateException> {
            service.save(Customer("Dave", "Matthews"))
        }

        repository.findByLastname("Matthews").firstOrNull().shouldBeNull()
    }

    @Test
    fun `insert data transactionally`() = runTest {
        // 저장된다.
        service.save(Customer("Carter", "Beauford")).hasId.shouldBeTrue()

        repository.findByLastname("Beauford").firstOrNull().shouldNotBeNull().hasId.shouldBeTrue()
    }
}
