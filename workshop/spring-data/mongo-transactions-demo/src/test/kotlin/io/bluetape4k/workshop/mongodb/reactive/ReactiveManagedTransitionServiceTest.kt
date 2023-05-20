package io.bluetape4k.workshop.mongodb.reactive

import com.mongodb.reactivestreams.client.MongoClient
import io.bluetape4k.logging.KLogging
import io.bluetape4k.support.uninitialized
import io.bluetape4k.workshop.mongodb.AbstractMongodbTest
import io.bluetape4k.workshop.mongodb.State
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
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories
import org.springframework.test.context.ContextConfiguration
import org.springframework.transaction.ReactiveTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement
import reactor.core.publisher.Flux
import reactor.test.StepVerifier

@ContextConfiguration(classes = [ReactiveManagedTransitionServiceTest.TestConfig::class])
class ReactiveManagedTransitionServiceTest: AbstractMongodbTest() {

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
    private val managedTransitionService: ReactiveManagedTransitionService = uninitialized()

    @Autowired
    private val client: MongoClient = uninitialized()

    @Autowired
    private val repository: ReactiveProcessRepository = uninitialized()

    @BeforeEach
    fun beforeEach() {
        repository.deleteAll().block()
    }

    @Test
    fun `context loading`() {
        managedTransitionService.shouldNotBeNull()
        client.shouldNotBeNull()
        repository.shouldNotBeNull()
    }

    @Test
    fun `reactive transaction commit and rollback`() {
        repeat(10) {
            managedTransitionService.newProcess()
                .map { it.id }
                .flatMap { managedTransitionService.run(it) }
                .onErrorReturn(-1).`as`(StepVerifier::create)
                .consumeNextWith { x -> }
                .verifyComplete()
        }

        val documents = client
            .getDatabase(DATABASE_NAME)
            .getCollection("processes")
            .find(org.bson.Document())

        Flux.from(documents)
            .buffer(10)
            .`as`(StepVerifier::create)
            .consumeNextWith { docs ->
                docs.forEach { doc ->
                    println("document: $doc")

                    if (doc.getInteger("_id") % 3 == 0) {
                        doc.getString("state") shouldBeEqualTo State.CREATED.toString()
                    } else {
                        doc.getString("state") shouldBeEqualTo State.DONE.toString()
                    }
                }
            }
            .verifyComplete()
    }
}
