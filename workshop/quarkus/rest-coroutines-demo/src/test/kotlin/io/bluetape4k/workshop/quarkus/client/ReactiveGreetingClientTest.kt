package io.bluetape4k.workshop.quarkus.client

import io.bluetape4k.logging.KLogging
import io.quarkus.test.junit.QuarkusTest
import io.smallrye.mutiny.coroutines.awaitSuspending
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeTrue
import org.amshove.kluent.shouldHaveSize
import org.eclipse.microprofile.rest.client.inject.RestClient
import org.junit.jupiter.api.Test
import jakarta.inject.Inject

@QuarkusTest
class ReactiveGreetingClientTest {

    companion object: KLogging()

    @Inject
    @RestClient
    lateinit var client: ReactiveGreetingClient

    @Test
    fun `call hello`() {
        client.hello() shouldBeEqualTo "hello quarkus!"
    }

    @Test
    fun `call greeting`() = runTest {
        val result = client.greeting("BTS").awaitSuspending()
        result.message shouldBeEqualTo "Hello BTS"
    }

    @Test
    fun `greetings as Uni`() = runTest {
        val count = 10
        val greetings = client.greetings(count, "BTS").awaitSuspending()

        greetings shouldHaveSize count
        greetings.all { it.message.contains("Hello BTS") }.shouldBeTrue()
    }

    @Test
    fun `greeting stream`() = runTest {
        val count = 10
        val greetings = client.stream(count, "BTS").collect().asList().awaitSuspending()

        greetings shouldHaveSize count
        greetings.all { it.message.contains("Hello BTS") }.shouldBeTrue()
    }
}
