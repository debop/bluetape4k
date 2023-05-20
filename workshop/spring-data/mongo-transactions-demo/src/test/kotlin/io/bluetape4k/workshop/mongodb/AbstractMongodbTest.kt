package io.bluetape4k.workshop.mongodb

import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import io.bluetape4k.junit5.faker.Fakers
import io.bluetape4k.logging.KLogging
import io.bluetape4k.testcontainers.storage.MongoDBServer
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
abstract class AbstractMongodbTest {

    companion object: KLogging() {
        @JvmStatic
        val mongodb = MongoDBServer.Launcher.mongoDB

        @JvmStatic
        val faker = Fakers.faker

        fun createMongoClient(): MongoClient = MongoClients.create(mongodb.url)

        fun createReactiveMongoClient(): com.mongodb.reactivestreams.client.MongoClient =
            com.mongodb.reactivestreams.client.MongoClients.create(mongodb.url)
    }

}
