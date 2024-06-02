package io.bluetape4k.workshop.micrometer.zipkin

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.testcontainers.infrastructure.ZipkinServer
import io.bluetape4k.utils.Systemx
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeTrue
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.Test
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import kotlin.time.Duration.Companion.seconds

class ZipkinServerLaunchTest {

    companion object: KLogging()

    private val webClient = WebClient.create()

    @Test
    fun `launch zipkin server`() = runTest(timeout = 30.seconds) {

        ZipkinServer().use {
            it.start()
            it.isRunning.shouldBeTrue()

            val zipkinUrl = Systemx.getProp("testcontainers.zipkin.url")
            zipkinUrl.shouldNotBeNull() shouldBeEqualTo it.url

            val client = WebClient.builder().baseUrl(zipkinUrl).build()

            val response = client.get()
                .uri("/zipkin/")
                .retrieve()
                .bodyToMono<String>()
                .awaitSingleOrNull()

            log.debug { "response=$response" }
            response.shouldNotBeNull()
        }
    }
}
