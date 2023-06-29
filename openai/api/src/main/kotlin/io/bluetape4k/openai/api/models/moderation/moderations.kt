package io.bluetape4k.openai.api.models.moderation

import com.fasterxml.jackson.annotation.JsonProperty
import io.bluetape4k.core.requireNotNull
import io.bluetape4k.openai.api.annotations.OpenAIDsl
import java.io.Serializable

/**
 * Moderation model.
 */
@JvmInline
value class ModerationModel(val model: String): Serializable {

    companion object {

        /**
         * Ensures you are always using the most accurate model.
         */
        val Stable: ModerationModel = ModerationModel("text-moderation-stable")

        /**
         * Advanced notice is provided before updating this model.
         */
        val Latest: ModerationModel = ModerationModel("text-moderation-latest")

        /**
         * Default moderation model
         */
        val Default: ModerationModel = Latest
    }
}

/**
 * Request to classify if text violates OpenAI's Content Policy.
 *
 * @property input The input text to classify.
 * @property model Moderation model. Defaults to [ModerationModel.Latest].
 */
data class ModerationRequest(
    val input: String,
    val model: ModerationModel? = ModerationModel.Default,
): Serializable

inline fun moderationRequest(initializer: ModerationRequestBuilder.() -> Unit): ModerationRequest =
    ModerationRequestBuilder().apply(initializer).build()

/**
 * Data class representing a ModerationRequest
 */
@OpenAIDsl
class ModerationRequestBuilder {

    /**
     * The input text to classify.
     */
    var input: String? = null

    /**
     * Moderation model. Defaults to [ModerationModel.Latest].
     */
    var model: ModerationModel? = null

    fun build(): ModerationRequest = ModerationRequest(
        input = input.requireNotNull("input"),
        model = model ?: ModerationModel.Default
    )
}

/**
 * An object containing a response from the moderation api
 *
 * 참고: [Moderations Create](https://beta.openai.com/docs/api-reference/moderations/create)
 *
 * @property id A unique id assigned to this moderation
 * @property model The model used.
 * @property results A list of moderation scores.
 */
data class ModerationResult(
    val id: String,
    val model: ModerationModel,
    val results: List<Moderation>,
): Serializable

/**
 * An object containing the moderation data for a single input string
 *
 * 참고: [Moderations Create](https://beta.openai.com/docs/api-reference/moderations/create)
 *
 * @property categories        Object containing per-category binary content policy violation flags. For each category, the value is true if the model flags the corresponding category as violated, false otherwise.
 * @property categoryScores    Object containing per-category raw scores output by the model, denoting the model's confidence that the input violates the OpenAI's policy for the category. The value is between 0 and 1, where higher values denote higher confidence. The scores should not be interpreted as probabilities.
 * @property flagged            Set to true if the model classifies the content as violating OpenAI's content policy, false otherwise
 */
data class Moderation(
    val categories: ModerationCategories,
    val categoryScores: ModerationCategoryScores,
    val flagged: Boolean,
): Serializable

/**
 * An object containing the flags for each moderation category
 *
 * 참고: [Moderations Create](https://beta.openai.com/docs/api-reference/moderations/create)
 */
data class ModerationCategories(
    /**
     * Content that expresses, incites, or promotes hate based on race, gender, ethnicity, religion, nationality, sexual
     * orientation, disability status, or caste.
     */
    val hate: Boolean,

    /**
     * Hateful content that also includes violence or serious harm towards the targeted group.
     */
    @get:JsonProperty("hate/threatening")
    val hateThreatening: Boolean,

    /**
     * Content that promotes, encourages, or depicts acts of self-harm, such as suicide, cutting, and eating disorders.
     */
    @get:JsonProperty("self-harm")
    val selfHarm: Boolean,

    /**
     * 	Content meant to arouse sexual excitement, such as the description of sexual activity, or that promotes sexual
     * 	services (excluding sex education and wellness).
     */
    val sexual: Boolean,

    /**
     * Sexual content that includes an individual who is under 18 years old.
     */
    @get:JsonProperty("sexual/minors")
    val sexualMinors: Boolean,

    /**
     * Content that promotes or glorifies violence or celebrates the suffering or humiliation of others.
     */
    val violence: Boolean,

    /**
     * Violent content that depicts death, violence, or serious physical injury in extreme graphic detail.
     */
    @get:JsonProperty("violence/graphic")
    val violenceGraphic: Boolean,
): Serializable

/**
 * An object containing the scores for each moderation category
 *
 * 참고: [Moderation Create](https://beta.openai.com/docs/api-reference/moderations/create)
 */
data class ModerationCategoryScores(
    /**
     * Content that expresses, incites, or promotes hate based on race, gender, ethnicity, religion, nationality, sexual
     * orientation, disability status, or caste.
     */
    val hate: Double,

    /**
     * Hateful content that also includes violence or serious harm towards the targeted group.
     */
    @get:JsonProperty("hate/threatening")
    val hateThreatening: Double,

    /**
     * Content that promotes, encourages, or depicts acts of self-harm, such as suicide, cutting, and eating disorders.
     */
    @get:JsonProperty("self-harm")
    val selfHarm: Double,

    /**
     * 	Content meant to arouse sexual excitement, such as the description of sexual activity, or that promotes sexual
     * 	services (excluding sex education and wellness).
     */
    val sexual: Double,

    /**
     * Sexual content that includes an individual who is under 18 years old.
     */
    @get:JsonProperty("sexual/minors")
    val sexualMinors: Double,

    /**
     * Content that promotes or glorifies violence or celebrates the suffering or humiliation of others.
     */
    val violence: Double,

    /**
     * Violent content that depicts death, violence, or serious physical injury in extreme graphic detail.
     */
    @get:JsonProperty("violence/graphic")
    val violenceGraphic: Double,
): Serializable
