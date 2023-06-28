package io.bluetape4k.openai.api

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import io.bluetape4k.io.json.jackson.Jackson
import io.bluetape4k.io.utils.Resourcex
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.openai.api.annotations.BetaOpenAI
import io.bluetape4k.openai.api.models.audio.Transcription
import io.bluetape4k.openai.api.models.audio.TranscriptionRequest
import io.bluetape4k.openai.api.models.audio.Translation
import io.bluetape4k.openai.api.models.audio.TranslationRequest
import io.bluetape4k.openai.api.models.model.Model
import io.bluetape4k.openai.api.models.moderation.ModerationRequest
import io.bluetape4k.openai.api.models.moderation.ModerationResult
import io.bluetape4k.support.cast
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldNotBeEmpty
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

@BetaOpenAI
class OpenAIApiModelJsonTest {

    companion object : KLogging()

    private val mapper = Jackson.defaultJsonMapper.rebuild()
        .propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
        .build()

    @ParameterizedTest
    @ValueSource(
        classes = [
            Model::class,
            ModerationRequest::class,
            ModerationResult::class,
            TranscriptionRequest::class,
            Transcription::class,
            TranslationRequest::class,
            Translation::class,
        ]
    )
    fun `parse json text`(clazz: Class<*>) {
        val path = "fixtures/${clazz.simpleName}.json"
        val json = Resourcex.getString(path)
        log.debug { "path=$path, json=$json" }
        json.shouldNotBeEmpty()

        val model = mapper.readValue(json, clazz).cast(clazz.kotlin)
        model.shouldNotBeNull()
        log.debug { "model=$model" }

        val actual = mapper.writeValueAsString(model)
        val actualModel = mapper.readValue(actual, clazz).cast(clazz.kotlin)
        actualModel.shouldNotBeNull()

        // Convert to JsonNodes to avoid any json formatting differences
        mapper.readValue(actual, clazz) shouldBeEqualTo mapper.readValue(json, clazz)
    }
}
