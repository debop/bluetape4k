package io.bluetape4k.workshop.vertx.webclient

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.vertx.tests.withTestContextSuspending
import io.vertx.core.Vertx
import io.vertx.ext.web.client.WebClient
import io.vertx.ext.web.codec.BodyCodec
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.coroutines.await
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(VertxExtension::class)
class CoroutineExamples {

    companion object: KLogging() {
        private const val port: Int = 9988
    }

    class CoroutineServer: CoroutineVerticle() {
        override suspend fun start() {
            vertx.createHttpServer()
                .requestHandler { req ->
                    req.response().end("Hello Coroutines!")
                }
                .listen(port)
                .await()
        }
    }

    @Test
    fun `use webclient to simple server`(vertx: Vertx, testContext: VertxTestContext) {
        vertx.withTestContextSuspending(testContext) {
            vertx.deployVerticle(CoroutineServer()).await()

            val client = WebClient.create(vertx)
            val response = client.get(port, "localhost", "/")
                .`as`(BodyCodec.string())
                .send()
                .await()

            log.debug { "Response body=${response.body()}" }
            response.body() shouldBeEqualTo "Hello Coroutines!"
        }
    }
}
