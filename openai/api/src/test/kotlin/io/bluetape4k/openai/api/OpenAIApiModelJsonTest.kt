package io.bluetape4k.openai.api

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import io.bluetape4k.json.jackson.Jackson
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.openai.api.annotations.BetaOpenAI
import io.bluetape4k.openai.api.models.DeleteResult
import io.bluetape4k.openai.api.models.audio.TranscriptionRequest
import io.bluetape4k.openai.api.models.audio.TranscriptionResult
import io.bluetape4k.openai.api.models.audio.TranslationRequest
import io.bluetape4k.openai.api.models.audio.TranslationResult
import io.bluetape4k.openai.api.models.chat.ChatCompletionRequest
import io.bluetape4k.openai.api.models.chat.ChatCompletionResult
import io.bluetape4k.openai.api.models.completion.CompletionRequest
import io.bluetape4k.openai.api.models.completion.CompletionResult
import io.bluetape4k.openai.api.models.edits.EditRequest
import io.bluetape4k.openai.api.models.edits.EditResult
import io.bluetape4k.openai.api.models.embedding.EmbeddingRequest
import io.bluetape4k.openai.api.models.embedding.EmbeddingResult
import io.bluetape4k.openai.api.models.file.File
import io.bluetape4k.openai.api.models.finetune.FineTuneEvent
import io.bluetape4k.openai.api.models.finetune.FineTuneResult
import io.bluetape4k.openai.api.models.image.ImageCreationRequest
import io.bluetape4k.openai.api.models.image.ImageEditRequest
import io.bluetape4k.openai.api.models.image.ImageResult
import io.bluetape4k.openai.api.models.image.ImageVariationRequest
import io.bluetape4k.openai.api.models.model.Model
import io.bluetape4k.openai.api.models.moderation.ModerationRequest
import io.bluetape4k.openai.api.models.moderation.ModerationResult
import io.bluetape4k.support.cast
import io.bluetape4k.utils.Resourcex
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldNotBeEmpty
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

@BetaOpenAI
class OpenAIApiModelJsonTest {

    companion object: KLogging()

    private val mapper = Jackson.defaultJsonMapper.rebuild()
        .propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
        .build()

    @ParameterizedTest
    @ValueSource(
        classes = [
            ChatCompletionRequest::class,
            ChatCompletionResult::class,
            CompletionRequest::class,
            CompletionResult::class,
            DeleteResult::class,
            EditRequest::class,
            EditResult::class,
            EmbeddingRequest::class,
            EmbeddingResult::class,
            File::class,
            FineTuneEvent::class,
            FineTuneResult::class,
            ImageCreationRequest::class,
            ImageEditRequest::class,
            ImageResult::class,
            ImageVariationRequest::class,
            Model::class,
            ModerationRequest::class,
            ModerationResult::class,
            TranscriptionRequest::class,
            TranscriptionResult::class,
            TranslationRequest::class,
            TranslationResult::class,
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
