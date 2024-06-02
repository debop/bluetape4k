package io.bluetape4k.openai.client.model.moderation

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

data class ModerationResponse(
    @JsonProperty("id") val id: String,
    @JsonProperty("model") val model: String,
    @JsonProperty("results") val results: List<ModerationResult>,
): Serializable


data class ModerationResult(
    @JsonProperty("flagged") val flagged: Boolean,
    @JsonProperty("categories") val categories: Categories,
    @JsonProperty("category_scores") val categoryScores: CategoryScores,
)

data class Categories(
    @JsonProperty("sexual") val sexual: Boolean? = null,
    @JsonProperty("hate") val hate: Boolean? = null,
    @JsonProperty("harassment") val harassment: Boolean? = null,
    @JsonProperty("self-harm") val selfHarm: Boolean? = null,
    @JsonProperty("sexual/minors") val sexualMinors: Boolean? = null,
    @JsonProperty("hate/threatening") val hateThreatening: Boolean? = null,
    @JsonProperty("violence/graphic") val violenceGraphic: Boolean? = null,
    @JsonProperty("self-harm/intent") val selfHarmIntent: Boolean? = null,
    @JsonProperty("self-harm/instructions") val selfHarmInstructions: Boolean? = null,
    @JsonProperty("harassment/threatening") val harassmentThreatening: Boolean? = null,
    @JsonProperty("violence") val violence: Boolean? = null,
): Serializable

data class CategoryScores(
    @JsonProperty("sexual") val sexual: Double? = null,
    @JsonProperty("hate") val hate: Double? = null,
    @JsonProperty("harassment") val harassment: Double? = null,
    @JsonProperty("self-harm") val selfHarm: Double? = null,
    @JsonProperty("sexual/minors") val sexualMinors: Double? = null,
    @JsonProperty("hate/threatening") val hateThreatening: Double? = null,
    @JsonProperty("violence/graphic") val violenceGraphic: Double? = null,
    @JsonProperty("self-harm/intent") val selfHarmIntent: Double? = null,
    @JsonProperty("self-harm/instructions") val selfHarmInstructions: Double? = null,
    @JsonProperty("harassment/threatening") val harassmentThreatening: Double? = null,
    @JsonProperty("violence") val violence: Double? = null,
): Serializable
