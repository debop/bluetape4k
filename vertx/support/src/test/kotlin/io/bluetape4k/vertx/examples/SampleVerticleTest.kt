package io.bluetape4k.vertx.examples

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.vertx.tests.withTestContextAwait
import io.vertx.core.Vertx
import io.vertx.ext.web.client.HttpResponse
import io.vertx.ext.web.client.WebClient
import io.vertx.ext.web.codec.BodyCodec
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import io.vertx.kotlin.coroutines.awaitResult
import kotlinx.coroutines.launch
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldContain
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.concurrent.atomic.AtomicInteger

@ExtendWith(VertxExtension::class)
class SampleVerticleTest {

    companion object : KLogging()

    @Test
    fun `count three ticks`(vertx: Vertx, testContext: VertxTestContext) {
        val counter = AtomicInteger()
        vertx.setPeriodic(100) {
            if (counter.incrementAndGet() == 3) {
                testContext.completeNow()
            }
        }
    }


    @Test
    fun `count three ticks with checkpoints`(vertx: Vertx, testContext: VertxTestContext) {
        val checkpoint = testContext.checkpoint(3)
        vertx.setPeriodic(100) {
            checkpoint.flag()
        }
    }

    @Test
    fun `use SampleVerticle`(vertx: Vertx, testContext: VertxTestContext) {
        val requestCount = 10
        val webClient = WebClient.create(vertx)
        val deploymentCheckpoint = testContext.checkpoint()
        val requestCheckpoint = testContext.checkpoint(requestCount)

        vertx.deployVerticle(SampleVerticle(), testContext.succeeding {
            deploymentCheckpoint.flag()

            repeat(requestCount) {
                webClient.get(11981, "localhost", "/")
                    .`as`(BodyCodec.string())
                    .send(testContext.succeeding { resp ->
                        testContext.verify {
                            resp.statusCode() shouldBeEqualTo 200
                            resp.body() shouldContain "Yo!"
                            requestCheckpoint.flag()
                        }
                    })
            }
        })
    }

    @Test
    fun `use SampleVerticle in coroutines`(vertx: Vertx, testContext: VertxTestContext) {
        withTestContextAwait(vertx, testContext) {
            val requestCount = 10
            val webClient = WebClient.create(vertx)
            val deploymentCheckpoint = testContext.checkpoint()
            val requestCheckpoint = testContext.checkpoint(requestCount)

            log.debug { "Deply SampleVerticle" }
            awaitResult<String> { vertx.deployVerticle(SampleVerticle(), it) }
            deploymentCheckpoint.flag()  //testContext 에게 현 단계까지 완료되었음을 알린다.

            repeat(requestCount) { requestIndex ->
                launch {
                    val resp = awaitResult<HttpResponse<String>> { handler ->
                        log.debug { "Request $requestIndex" }
                        webClient.get(11981, "localhost", "/")
                            .`as`(BodyCodec.string())
                            .send(handler)
                    }
                    testContext.verify {
                        resp.statusCode() shouldBeEqualTo 200
                        resp.body() shouldContain "Yo!"
                        // testContext에 완료되었음을 알린다 (CountDownLatch와 유사)
                        // 모두 차감하면 testContext.completeNow() 와 같이 테스트가 종료된다.
                        requestCheckpoint.flag()
                    }
                }
            }
        }
    }
}
