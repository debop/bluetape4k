package io.bluetape4k.workshop.mongodb.imperative

import com.mongodb.client.MongoClient
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Projections
import io.bluetape4k.logging.KLogging
import io.bluetape4k.support.uninitialized
import io.bluetape4k.workshop.mongodb.AbstractMongodbTest
import io.bluetape4k.workshop.mongodb.Process
import io.bluetape4k.workshop.mongodb.State
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.MongoDatabaseFactory
import org.springframework.data.mongodb.MongoTransactionManager
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories
import org.springframework.test.context.ContextConfiguration
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement

/**
 * MongoDB 에 Transaction을 사용하려면 MongoDB Replica 가 적용되던가 Mongos 에 Access 해야 합니다.
 */
@ContextConfiguration(classes = [TransitionServiceTest.TestConfig::class])
class TransitionServiceTest: AbstractMongodbTest() {

    companion object: KLogging() {
        private const val DATABASE_NAME = "spring-data-mongodb-transactions-demo"
    }

    @Configuration
    @ComponentScan
    @EnableMongoRepositories
    @EnableTransactionManagement
    class TestConfig: AbstractMongoClientConfiguration() {

        override fun getDatabaseName(): String = DATABASE_NAME

        @Bean
        override fun mongoClient(): MongoClient = createMongoClient()

        @Bean
        fun transactionManager(dbFactory: MongoDatabaseFactory): PlatformTransactionManager {
            return MongoTransactionManager(dbFactory)
        }
    }

    @Autowired
    private val transitionService: TransitionService = uninitialized()

    @Autowired
    private val client: MongoClient = uninitialized()

    @Autowired
    private val repository: ProcessRepository = uninitialized()

    @BeforeEach
    fun beforeEach() {
        repository.deleteAll()
    }

    @Test
    fun `context loading`() {
        transitionService.shouldNotBeNull()
        client.shouldNotBeNull()
        repository.shouldNotBeNull()
    }

    @Test
    fun `transaction commit and rollback`() {
        repeat(10) {
            val process: Process = transitionService.newProcess()
            try {
                transitionService.run(process.id)
                stateInDb(process) shouldBeEqualTo State.DONE
            } catch (e: IllegalStateException) {
                stateInDb(process) shouldBeEqualTo State.CREATED
            }
        }

        client.getDatabase(DATABASE_NAME).getCollection("processes")
            .find(org.bson.Document())
            .forEach {
                println("process=$it")
            }
    }

    private fun stateInDb(process: Process): State {
        val stateAsString = client.getDatabase(DATABASE_NAME).getCollection("processes")
            .find(Filters.eq("_id", process.id))
            .projection(Projections.include("state")).first()?.get("state", String::class.java)

        return if (stateAsString != null) State.valueOf(stateAsString) else State.UNKNOWN
    }
}
