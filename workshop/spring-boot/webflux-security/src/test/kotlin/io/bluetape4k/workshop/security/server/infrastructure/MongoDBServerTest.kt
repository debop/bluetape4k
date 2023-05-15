package io.bluetape4k.workshop.security.server.infrastructure

import io.bluetape4k.logging.KLogging
import io.bluetape4k.workshop.security.server.Application
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeTrue
import org.junit.jupiter.api.Test

class MongoDBServerTest {

    companion object: KLogging()

    @Test
    fun `MongoDBServer 실행`() {
        val mongodb = Application.mongoServer
        mongodb.isRunning.shouldBeTrue()
        mongodb.url shouldBeEqualTo System.getProperty("testcontainers.mongo.url")
    }
}
