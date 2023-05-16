package io.bluetape4k.workshop.quarkus.rest

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.quarkus.tests.restassured.bodyAs
import io.bluetape4k.quarkus.tests.restassured.bodyAsList
import io.bluetape4k.workshop.quarkus.model.Fruit
import io.quarkus.test.junit.QuarkusTest
import io.restassured.http.ContentType
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldNotBeEmpty
import org.jboss.resteasy.reactive.RestResponse
import org.junit.jupiter.api.Test

@QuarkusTest
class FruitResourceTest {

    companion object: KLogging()

    @Test
    fun `get all fruits`() {
        When {
            get("/fruits")
        }.Then {
            contentType(ContentType.JSON)
            statusCode(RestResponse.StatusCode.OK)
            val fruits = extract().bodyAsList<Fruit>()

            fruits.shouldNotBeEmpty()
            fruits.forEach {
                log.debug { it }
            }
        }
    }

    @Test
    fun `find fruit by name`() {
        Given {
            pathParam("name", "Cherry")
        }.When {
            get("/fruits/{name}")
        }.Then {
            contentType(ContentType.JSON)
            statusCode(RestResponse.StatusCode.OK)
            val fruit = extract().bodyAs<Fruit>()
            fruit.name shouldBeEqualTo "Cherry"
        }
    }

    @Test
    fun `find fruit by name not exists`() {
        Given {
            pathParam("name", "Not-Exists")
        }.When {
            get("/fruits/{name}")
        }.Then {
            statusCode(RestResponse.StatusCode.NO_CONTENT)
        }
    }

    @Test
    fun `add new fruit`() {
        val fruit = Fruit("Grape", "Juicy fruit")
        Given {
            contentType(ContentType.JSON)
            body(fruit)
        }.When {

            post("/fruits")
        }.Then {
            contentType(ContentType.JSON)
            statusCode(RestResponse.StatusCode.OK)
            val saved = extract().bodyAs<Fruit>()
            saved.name shouldBeEqualTo fruit.name
            saved.description shouldBeEqualTo fruit.description
        }
    }
}
