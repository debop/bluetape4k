package io.bluetape4k.openai.api.models.moderation

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

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
