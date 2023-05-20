package io.bluetape4k.workshop.mongodb.coroutine

import com.mongodb.client.model.Filters
import com.mongodb.client.model.Projections
import com.mongodb.reactivestreams.client.MongoClient
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.warn
import io.bluetape4k.support.uninitialized
import io.bluetape4k.workshop.mongodb.AbstractMongodbTest
import io.bluetape4k.workshop.mongodb.Process
import io.bluetape4k.workshop.mongodb.State
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.ReactiveMongoDatabaseFactory
import org.springframework.data.mongodb.ReactiveMongoTransactionManager
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration
import org.springframework.data.mongodb.core.ReactiveMongoOperations
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories
import org.springframework.test.context.ContextConfiguration
import org.springframework.transaction.ReactiveTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement

@ContextConfiguration(classes = [CoroutineManagedTransitionServiceTest.TestConfig::class])
class CoroutineManagedTransitionServiceTest: AbstractMongodbTest() {

    companion object: KLogging() {
        private const val DATABASE_NAME = "spring-data-mongodb-transactions-demo"
    }

    @Configuration
    @ComponentScan
    @EnableReactiveMongoRepositories
    @EnableTransactionManagement
    class TestConfig: AbstractReactiveMongoConfiguration() {
        override fun getDatabaseName(): String = DATABASE_NAME

        @Bean
        override fun reactiveMongoClient(): MongoClient = createReactiveMongoClient()

        @Bean
        fun transactionManager(dbFactory: ReactiveMongoDatabaseFactory): ReactiveTransactionManager {
            return ReactiveMongoTransactionManager(dbFactory)
        }
    }

    @Autowired
    private val managedTransitionService: CoroutineManagedTransitionService = uninitialized()

    @Autowired
    private val client: MongoClient = uninitialized()

    @Autowired
    private val operations: ReactiveMongoOperations = uninitialized()

    @Autowired
    private val repository: CoroutineProcessRepository = uninitialized()

    @BeforeEach
    fun beforeEach() {
        runBlocking {
            repository.deleteAll()
        }
    }

    @Test
    fun `context loading`() {
        managedTransitionService.shouldNotBeNull()
        client.shouldNotBeNull()
        repository.shouldNotBeNull()
    }

    @Test
    fun `coroutine transaction commit and rollback`() = runTest {
        repeat(10) {
            val process = managedTransitionService.newProcess()
            try {
                managedTransitionService.run(process.id)
                stateInDb(process) shouldBeEqualTo State.DONE
            } catch (e: IllegalStateException) {
                log.warn(e) { "작업 중 예외가 발생했습니다. process=$process" }
                stateInDb(process) shouldBeEqualTo State.CREATED
            }
        }

        client.getDatabase(DATABASE_NAME).getCollection("processes")
            .find(org.bson.Document())
            .asFlow()
            .collect {
                println("process=$it")
            }
    }

    private suspend fun stateInDb(process: Process): State {
        val stateAsString = client.getDatabase(DATABASE_NAME).getCollection("processes")
            .find(Filters.eq("_id", process.id))
            .projection(Projections.include("state"))
            .awaitFirst()
            .get("state", String::class.java)

        return if (stateAsString != null) State.valueOf(stateAsString) else State.UNKNOWN
    }
}
