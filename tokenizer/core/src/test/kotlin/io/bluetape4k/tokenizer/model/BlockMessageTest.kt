package io.bluetape4k.tokenizer.model

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.bluetape4k.logging.KLogging
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test

class BlockMessageTest {

    companion object: KLogging()

    private val mapper = jacksonObjectMapper()

    @Test
    fun `convert request to json`() {
        val origin = BlockwordRequest("요청 메시지", BlockwordOptions(severity = Severity.MIDDLE))

        val jsonText = mapper.writeValueAsString(origin)
        val actual = mapper.readValue<BlockwordRequest>(jsonText)

        actual shouldBeEqualTo origin
    }

    @Test
    fun `convert response to json`() {
        val request = BlockwordRequest("요청 메시지", BlockwordOptions(severity = Severity.MIDDLE))
        val origin = BlockwordResponse(request, "Masked 문자열", emptySet())

        val jsonText = mapper.writeValueAsString(origin)
        val actual = mapper.readValue<BlockwordResponse>(jsonText)

        actual shouldBeEqualTo origin
    }
}
