package io.bluetape4k.r2dbc.core

import io.bluetape4k.junit5.coroutines.runSuspendWithIO
import io.bluetape4k.logging.KLogging
import io.bluetape4k.r2dbc.AbstractR2dbcTest
import io.bluetape4k.r2dbc.model.User
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test
import org.springframework.data.r2dbc.core.allAndAwait
import org.springframework.data.relational.core.query.Criteria
import org.springframework.data.relational.core.query.Query
import org.springframework.r2dbc.core.awaitOne
import org.springframework.r2dbc.core.awaitRowsUpdated

class DeleteTest: AbstractR2dbcTest() {

    companion object: KLogging()

    @Test
    fun `delete record without entity class`() = runSuspendWithIO {
        client.insert().into("users")
            .value("username", "nick")
            .value("password", "pass")
            .value("name", "John Smith")
            .await()
        val count1 = client.execute<Int>("SELECT COUNT(*) FROM users").fetch().awaitOne()

        val rowsUpdated = client.delete().from("users")
            .matching("username = :username", mapOf("username" to "nick"))
            .fetch().awaitRowsUpdated()
        rowsUpdated shouldBeEqualTo 1

        val count2 = client.execute<Int>("SELECT COUNT(*) FROM users").fetch().awaitOne()
        (count1 - count2) shouldBeEqualTo 1
    }

    @Test
    fun `delete record with entity class`() = runSuspendWithIO {
        client.insert().into("users")
            .value("username", "nick")
            .value("password", "pass")
            .value("name", "John Smith")
            .await()
        val count1 = client.execute<Int>("SELECT COUNT(*) FROM users").fetch().awaitOne()

        val rowsUpdated = client.delete().from<User>()
            .matching(Query.query(Criteria.where("username").`is`("nick")))
            .allAndAwait()
        rowsUpdated shouldBeEqualTo 1

        val count2 = client.execute<Int>("SELECT COUNT(*) FROM users").fetch().awaitOne()
        (count1 - count2) shouldBeEqualTo 1
    }
}
