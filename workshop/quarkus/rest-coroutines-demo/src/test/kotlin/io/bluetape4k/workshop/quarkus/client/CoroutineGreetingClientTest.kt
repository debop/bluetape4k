package io.bluetape4k.workshop.quarkus.client

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.quarkus.test.common.http.TestHTTPResource
import io.quarkus.test.junit.QuarkusTest
import io.smallrye.mutiny.coroutines.asFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeTrue
import org.amshove.kluent.shouldHaveSize
import org.eclipse.microprofile.rest.client.RestClientBuilder
import org.eclipse.microprofile.rest.client.inject.RestClient
import org.junit.jupiter.api.Test
import java.net.URI
import javax.inject.Inject

@QuarkusTest
class CoroutineGreetingClientTest {

    companion object: KLogging()

    @Inject
    @RestClient
    internal lateinit var greetingClient: CoroutineGreetingClient

    @Test
    fun `hello by client`() = runTest {
        greetingClient.hello() shouldBeEqualTo "hello quarkus!"
    }

    @Test
    fun `greeting by client`() = runTest {
        val result = greetingClient.greeting("BTS")
        result.message shouldBeEqualTo "Hello BTS"
    }

    @Test
    fun `greetings by client`() = runTest {
        val count = 10
        val greetings = greetingClient.greetings(count, "BTS")

        greetings shouldHaveSize count
        greetings.all { it.message.contains("Hello BTS") }.shouldBeTrue()
    }

    @Test
    fun `greetings as stream by client`() = runTest {
        val count = 10
        val greetings = greetingClient.greetingAsStream(count, "BTS").asFlow()
            .onEach { log.debug { "receive $it" } }
            .toList()

        greetings shouldHaveSize count
        greetings.all { it.message.contains("Hello BTS") }.shouldBeTrue()
    }

    @TestHTTPResource
    internal lateinit var uri: URI

    /**
     * 프로그래밍 방식으로 RestClient를 빌드합니다.
     */
    private val greeting by lazy {
        log.debug { "uri=$uri" }
        RestClientBuilder.newBuilder()
            .baseUri(uri)
            .build(CoroutineGreetingClient::class.java)
    }

    @Test
    fun `greeting by programming client`() = runTest {
        greeting.greeting("BTS").message shouldBeEqualTo "Hello BTS"
    }

}
