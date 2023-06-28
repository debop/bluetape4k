package io.bluetape4k.openai.api.models.embedding

import io.bluetape4k.core.requireNotNull
import io.bluetape4k.openai.api.annotations.OpenAIDsl
import io.bluetape4k.openai.api.models.ModelBuilder
import io.bluetape4k.openai.api.models.model.ModelId
import java.io.Serializable

/**
 * Create an embedding request
 *
 * 참고: [embeddings](https://beta.openai.com/docs/api-reference/embeddings)
 *
 * @property model ID of the model to use
 * @property input Input text to get embeddings for, encoded as an array of token. Each input must not exceed 2048 tokens in length.
 *                 Unless you are embedding code, we suggest replacing newlines (`\n`) in your input with a single space,
 *                 as we have observed inferior results when newlines are present.
 * @property user   A unique identifier representing your end-user, which will help OpenAI to monitor and detect abuse.
 */
data class EmbeddingRequest(
    val model: ModelId,
    val input: List<String>,
    val user: String? = null,
) : Serializable

@OpenAIDsl
class EmbeddingRequestBuilder : ModelBuilder<EmbeddingRequest> {
    var model: ModelId? = null
    var input: List<String>? = null
    var user: String? = null

    override fun build(): EmbeddingRequest {
        return EmbeddingRequest(
            model = this.model.requireNotNull("model"),
            input = this.input.requireNotNull("input"),
            user = this.user
        )
    }
}

inline fun embeddingRequest(initializer: EmbeddingRequestBuilder.() -> Unit): EmbeddingRequest =
    EmbeddingRequestBuilder().apply(initializer).build()
