package io.bluetape4k.tokenizer.model

import com.fasterxml.jackson.module.kotlin.readValue
import io.bluetape4k.io.json.jackson.Jackson
import io.bluetape4k.junit5.faker.Fakers
import io.bluetape4k.logging.KLogging
import net.datafaker.Faker
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test

class BlockMessageTest {

    companion object : KLogging() {
        val faker = Faker()
    }

    private val mapper = Jackson.defaultJsonMapper

    private fun newRequest(severity: Severity = Severity.MIDDLE): BlockwordRequest {
        return BlockwordRequest(
            Fakers.randomString(16, 1024, true),
            BlockwordOptions(severity = severity)
        )
    }

    @Test
    fun `convert request to json`() {
        val origin = newRequest()

        val jsonText = mapper.writeValueAsString(origin)
        val actual = mapper.readValue<BlockwordRequest>(jsonText)

        actual shouldBeEqualTo origin
    }

    @Test
    fun `convert response to json`() {
        val request = newRequest()
        val origin = BlockwordResponse(request, "Masked 문자열", emptySet())

        val jsonText = mapper.writeValueAsString(origin)
        val actual = mapper.readValue<BlockwordResponse>(jsonText)

        actual shouldBeEqualTo origin
    }
}
