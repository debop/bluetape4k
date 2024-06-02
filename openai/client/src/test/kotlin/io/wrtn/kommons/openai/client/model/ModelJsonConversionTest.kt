package io.bluetape4k.openai.client.model

import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.module.kotlin.readValue
import io.bluetape4k.json.jackson.Jackson
import io.bluetape4k.json.jackson.prettyWriteAsString
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.openai.client.model.audio.TranscriptionResponse
import io.bluetape4k.openai.client.model.audio.TranslationResponse
import io.bluetape4k.openai.client.model.chat.ChatCompletionChunk
import io.bluetape4k.openai.client.model.chat.ChatCompletionRequest
import io.bluetape4k.openai.client.model.chat.ChatCompletionResponse
import io.bluetape4k.openai.client.model.completions.CompletionRequest
import io.bluetape4k.openai.client.model.completions.CompletionResponse
import io.bluetape4k.openai.client.model.core.DeleteResponse
import io.bluetape4k.openai.client.model.edit.EditRequest
import io.bluetape4k.openai.client.model.edit.EditResponse
import io.bluetape4k.openai.client.model.embeddnings.EmbeddingRequest
import io.bluetape4k.openai.client.model.embeddnings.EmbeddingResponse
import io.bluetape4k.openai.client.model.file.File
import io.bluetape4k.openai.client.model.finetune.FineTuneEvent
import io.bluetape4k.openai.client.model.finetune.FineTuneEventList
import io.bluetape4k.openai.client.model.finetune.FineTuneRequest
import io.bluetape4k.openai.client.model.finetune.FineTuneResponse
import io.bluetape4k.openai.client.model.images.CreateImageRequest
import io.bluetape4k.openai.client.model.images.ImageResponse
import io.bluetape4k.openai.client.model.model.Model
import io.bluetape4k.openai.client.model.moderation.ModerationRequest
import io.bluetape4k.openai.client.model.moderation.ModerationResponse
import io.bluetape4k.utils.Resourcex
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource


class ModelJsonConversionTest {

    companion object: KLogging() {

        const val FIXTURES_PATH = "/fixtures/"
    }

    private val mapper = Jackson.defaultJsonMapper.apply {
        // this.setSerializationInclusion(JsonInclude.Include.NON_NULL)
        serializationConfig.with(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS, true)
    }

    @ParameterizedTest(name = "parse {0}")
    @ValueSource(
        classes = [
            ChatCompletionRequest::class,
            ChatCompletionResponse::class,
            ChatCompletionChunk::class,
            CompletionRequest::class,
            CompletionResponse::class,
            DeleteResponse::class,
            EditRequest::class,
            EditResponse::class,
            EmbeddingRequest::class,
            EmbeddingResponse::class,
            File::class,
            FineTuneEvent::class,
            FineTuneRequest::class,
            FineTuneResponse::class,
            CreateImageRequest::class,
            ImageResponse::class,
            Model::class,
            ModerationRequest::class,
            ModerationResponse::class,
            TranscriptionResponse::class,
            TranslationResponse::class,
        ]
    )
    fun `deserialize json text`(clazz: Class<*>) {
        val json = Resourcex.getString(FIXTURES_PATH + "${clazz.simpleName}.json")
        log.debug { "original json=\n$json" }

        val obj = mapper.readValue(json, clazz)
        val actual = mapper.prettyWriteAsString(obj)
        log.debug { "parsed node=\n$actual" }

        mapper.readValue(actual, clazz) shouldBeEqualTo obj
        mapper.readTree(actual) shouldBeEqualTo mapper.readTree(json)
    }

    @Test
    fun `deserialize FineTuneEventList`() {
        val filename = "FineTuneEventList.json"
        val json = Resourcex.getString(FIXTURES_PATH + filename)
        log.debug { "original json=\n$json" }

        val obj = mapper.readValue<FineTuneEventList>(json)
        val actual = mapper.prettyWriteAsString(obj)!!
        log.debug { "parsed node=\n$actual" }

        mapper.readTree(actual) shouldBeEqualTo mapper.readTree(json)
    }
}
