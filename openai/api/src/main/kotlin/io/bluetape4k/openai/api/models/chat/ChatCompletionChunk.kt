package io.bluetape4k.openai.api.models.chat

import io.bluetape4k.openai.api.models.Usage
import io.bluetape4k.openai.api.models.model.ModelId

data class ChatCompletionChunk(
    val id: String,
    val created: Long,
    val model: ModelId? = null,
    val choices: List<ChatChunk>,
    val usage: Usage? = null,
)
