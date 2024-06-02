package io.bluetape4k.openai.api.models.edits

import io.bluetape4k.openai.api.annotations.OpenAIDsl
import io.bluetape4k.openai.api.models.ModelBuilder
import io.bluetape4k.openai.api.models.model.ModelId
import io.bluetape4k.support.requireNotBlank
import io.bluetape4k.support.requireNotNull
import java.io.Serializable

data class EditRequest(
    val model: ModelId,
    val instruction: String,
    val input: String? = null,
    val temperature: Double? = null,
    val topP: Double? = null,
): Serializable

inline fun editsRequest(initializer: EditsRequestBuilder.() -> Unit): EditRequest {
    return EditsRequestBuilder().apply(initializer).build()
}

@OpenAIDsl
class EditsRequestBuilder: ModelBuilder<EditRequest> {

    var model: ModelId? = null
    var instruction: String? = null
    var input: String? = null
    var temperature: Double? = null
    var topP: Double? = null

    override fun build(): EditRequest {
        return EditRequest(
            model = model.requireNotNull("model"),
            instruction = instruction.requireNotBlank("instruction"),
            input = input,
            temperature = temperature,
            topP = topP
        )
    }
}
