package io.bluetape4k.openai.api.models

import com.fasterxml.jackson.module.kotlin.readValue
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.openai.api.AbstractApiTest
import io.bluetape4k.openai.api.annotations.BetaOpenAI
import io.bluetape4k.openai.api.models.audio.TranscriptionRequest
import io.bluetape4k.openai.api.models.audio.TranslationRequest
import io.bluetape4k.openai.api.models.audio.transcriptionRequest
import io.bluetape4k.openai.api.models.audio.translationRequest
import io.bluetape4k.openai.api.models.file.FileSource
import io.bluetape4k.openai.api.models.model.ModelId
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test

@BetaOpenAI
class AudioTest : AbstractApiTest() {

    companion object : KLogging()

    @Test
    fun `map TranslationRequest`() {
        val request = translationRequest {
            this.audio = FileSource("audio-name", "audio-file.mp4")
            this.model = ModelId("gpt3-turbo")
            this.prompt = "prompt"
            this.responseFormat = "application/json"
            this.temperature = 1.5
        }
        val json = mapper.writeValueAsString(request)
        log.debug { "json=$json" }

        val actual = mapper.readValue<TranslationRequest>(json)
        actual shouldBeEqualTo request
    }


    @Test
    fun `map TransriptionRequest`() {
        val request = transcriptionRequest {
            this.audio = FileSource("audio-name", "audio-file.mp4")
            this.model = ModelId("gpt3-turbo")
            this.prompt = "prompt"
            this.responseFormat = "application/json"
            this.temperature = 1.5
            this.language = "english"
        }
        val json = mapper.writeValueAsString(request)
        log.debug { "json=$json" }

        val actual = mapper.readValue<TranscriptionRequest>(json)
        actual shouldBeEqualTo request
    }
}
