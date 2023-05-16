package io.bluetape4k.workshop.quarkus

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.quarkus.tests.restassured.bodyAs
import io.bluetape4k.quarkus.tests.restassured.bodyAsList
import io.bluetape4k.workshop.quarkus.model.Greeting
import io.quarkus.test.junit.QuarkusTest
import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.amshove.kluent.shouldBeEqualTo
import org.hamcrest.CoreMatchers
import org.junit.jupiter.api.Test
import java.util.*
import javax.ws.rs.core.MediaType


@QuarkusTest
class CoroutineGreetingResourceTest {

    companion object: KLogging()

    @Test
    fun `call suspend as non-blocking`() {
        When {
            get("/coroutines")
        } Then {
            statusCode(200)
            body(CoreMatchers.`is`("hello quarkus!"))
        }
    }

    @Test
    fun `call sequential remote hello`() {
        When {
            get("/coroutines/sequential-hello")
        } Then {
            statusCode(200)
            body(CoreMatchers.containsString("hello"))
        }
    }

    @Test
    fun `greeting with suspend`() {
        val name = UUID.randomUUID().toString()

        Given {
            pathParam("name", name)
        } When {
            get("/coroutines/greeting/{name}")
        } Then {
            statusCode(200)
            body("message", CoreMatchers.equalTo("Hello $name"))
        } Extract {
            val greeting = bodyAs<Greeting>()
            greeting.message shouldBeEqualTo "Hello $name"
        }
    }

    @Test
    fun `sequential greeting`() {
        val name = UUID.randomUUID().toString()

        Given {
            pathParam("name", name)
        } When {
            get("/coroutines/sequential-greeting/{name}")
        } Then {
            statusCode(200)
            body(CoreMatchers.containsString("Hello $name"))
        } Extract {
            val greeting = bodyAsList<Greeting>()
            greeting.first().message shouldBeEqualTo "Hello $name"
        }
    }

    @Test
    fun `greetings as Flow`() {
        val count = 2
        val name = UUID.randomUUID().toString()

        Given {
            pathParam("count", count)
            pathParam("name", name)
        } When {
            get("/coroutines/greeting/{count}/{name}")
        } Then {
            statusCode(200)
            body(CoreMatchers.containsString("Hello $name - 0"))
            body(CoreMatchers.containsString("Hello $name - 1"))
        } Extract {
            val greetings = bodyAsList<Greeting>()
            log.debug { greetings }
            greetings.forEachIndexed { index, greeting ->
                greeting.message shouldBeEqualTo "Hello $name - $index"
            }
        }
    }

    @Test
    fun `greetings as server-sent-events`() {
        val count = 2
        val name = UUID.randomUUID().toString()

        Given {
            contentType(MediaType.SERVER_SENT_EVENTS)
            pathParam("count", count)
            pathParam("name", name)
        }.When {
            get("/coroutines/stream/{count}/{name}")
        }.Then {
            statusCode(200)
            body(CoreMatchers.containsString("Hello $name - 0"))
            body(CoreMatchers.containsString("Hello $name - 1"))
        } Extract {
            val bodyStr = body().asPrettyString()
            log.debug { "body=\n$bodyStr" }
        }
    }
}
