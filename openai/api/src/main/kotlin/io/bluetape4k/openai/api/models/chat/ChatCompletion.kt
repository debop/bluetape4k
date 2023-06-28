package io.bluetape4k.openai.api.models.chat

import io.bluetape4k.openai.api.annotations.BetaOpenAI
import io.bluetape4k.openai.api.models.Usage
import io.bluetape4k.openai.api.models.model.ModelId
import java.io.Serializable

@BetaOpenAI
data class ChatCompletion(
    val id: String,
    val created: Long,
    val model: ModelId,
    val choices: List<ChatChoice>,
    val usage: Usage? = null,
) : Serializable
