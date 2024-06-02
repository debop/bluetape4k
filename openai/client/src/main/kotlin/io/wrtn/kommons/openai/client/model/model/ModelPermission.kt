package io.bluetape4k.openai.client.model.model

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

data class ModelPermission(
    @JsonProperty("id") val id: String,
    @JsonProperty("created") val created: Long,
    @JsonProperty("allow_create_engine") val allowCreateEngine: Boolean? = null,
    @JsonProperty("allow_sampling") val allowSampling: Boolean? = null,
    @JsonProperty("allow_logprobs") val allowLogprobs: Boolean? = null,
    @JsonProperty("allow_search_indices") val allowSearchIndices: Boolean? = null,
    @JsonProperty("allow_view") val allowView: Boolean? = null,
    @JsonProperty("allow_fine_tuning") val allowFineTuning: Boolean? = null,
    @JsonProperty("organization") val organization: String? = null,
    @JsonProperty("is_blocking") val isBlocking: Boolean? = null,
): Serializable
