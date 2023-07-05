package io.bluetape4k.openai.api.models

import com.fasterxml.jackson.module.kotlin.readValue
import io.bluetape4k.logging.debug
import io.bluetape4k.openai.api.AbstractApiTest
import io.bluetape4k.openai.api.models.moderation.ModerationRequest
import io.bluetape4k.openai.api.models.moderation.moderationRequest
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldNotBeEmpty
import org.junit.jupiter.api.Test

class ModerationTest : AbstractApiTest() {

    @Test
    fun `build moderationRequest`() {
        val request = moderationRequest {
            input = "I want to kill them."
        }

        val json = mapper.writeValueAsString(request)
        log.debug { "json=$json" }
        json.shouldNotBeEmpty()

        val actual = mapper.readValue<ModerationRequest>(json)
        actual shouldBeEqualTo request
    }
}
