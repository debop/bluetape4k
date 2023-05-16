package io.bluetape4k.workshop.quarkus

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.quarkus.tests.restassured.bodyAs
import io.bluetape4k.quarkus.tests.restassured.bodyAsList
import io.bluetape4k.workshop.quarkus.model.Greeting
import io.quarkus.test.junit.QuarkusTest
import io.restassured.http.ContentType
import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.amshove.kluent.shouldBeEqualTo
import org.hamcrest.CoreMatchers
import org.jboss.resteasy.reactive.RestResponse
import org.junit.jupiter.api.Test
import java.util.*
import javax.ws.rs.core.MediaType

/**
 * `rest-assured` 를 이용하여 REST API 테스트를 수행합니다
 *
 * 참고: [REST-assured](https://rest-assured.io/)
 */
@QuarkusTest
class ReactiveGreetingResourceTest {

    companion object: KLogging()

    @Test
    fun `call as blocking`() {
        When {
            get("/reactive")
        }.Then {
            statusCode(200)
            body(CoreMatchers.`is`("hello quarkus!"))
        }
    }

    @Test
    fun `greeting as uni`() {
        val name = UUID.randomUUID().toString()
        Given {
            pathParam("name", name)
        } When {
            get("/reactive/greeting/{name}")
        } Then {
            statusCode(200)
            body("message", CoreMatchers.equalTo("Hello $name"))
        } Extract {
            val greeting = bodyAs<Greeting>()
            greeting.message shouldBeEqualTo "Hello $name"
        }
    }

    @Test
    fun `greetings as Multi`() {
        val count = 2
        val name = UUID.randomUUID().toString()

        Given {
            pathParam("count", count)
            pathParam("name", name)
        } When {
            get("/reactive/greeting/{count}/{name}")
        } Then {
            statusCode(200)
            contentType(ContentType.JSON)
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
        } When {
            get("/reactive/stream/{count}/{name}")
        } Then {
            statusCode(RestResponse.StatusCode.OK)
            body(CoreMatchers.containsString("Hello $name - 0"))
            body(CoreMatchers.containsString("Hello $name - 1"))
        } Extract {
            val bodyStr = body().asPrettyString()
            log.debug { "body=\n$bodyStr" }
        }
    }
}
