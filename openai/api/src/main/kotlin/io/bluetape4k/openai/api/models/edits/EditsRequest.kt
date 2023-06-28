package io.bluetape4k.openai.api.models.edits

import io.bluetape4k.core.requireNotBlank
import io.bluetape4k.core.requireNotNull
import io.bluetape4k.openai.api.annotations.OpenAIDsl
import io.bluetape4k.openai.api.models.ModelBuilder
import io.bluetape4k.openai.api.models.model.ModelId
import java.io.Serializable

data class EditsRequest(
    val model: ModelId,
    val instruction: String,
    val input: String? = null,
    val temperature: Double? = null,
    val topP: Double? = null,
) : Serializable

@OpenAIDsl
class EditsRequestBuilder : ModelBuilder<EditsRequest> {

    var model: ModelId? = null
    var instruction: String? = null
    var input: String? = null
    var temperature: Double? = null
    var topP: Double? = null

    override fun build(): EditsRequest {
        return EditsRequest(
            model = model.requireNotNull("model"),
            instruction = instruction.requireNotBlank("instruction"),
            input = input,
            temperature = temperature,
            topP = topP
        )
    }
}

inline fun editsRequest(initializer: EditsRequestBuilder.() -> Unit): EditsRequest =
    EditsRequestBuilder().apply(initializer).build()
