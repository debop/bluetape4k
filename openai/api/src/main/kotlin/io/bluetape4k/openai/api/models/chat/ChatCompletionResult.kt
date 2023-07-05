package io.bluetape4k.openai.api.models.chat

import com.fasterxml.jackson.annotation.JsonProperty
import io.bluetape4k.openai.api.models.ObjectId
import io.bluetape4k.openai.api.models.Usage
import io.bluetape4k.openai.api.models.model.ModelId
import java.io.Serializable

data class ChatCompletionResult(
    val id: String,
    @get:JsonProperty("object")
    val objectId: ObjectId? = null,
    val created: Long,
    val mode: ModelId? = null,
    val choices: List<ChatChoice>,
    val usage: Usage? = null,
): Serializable
