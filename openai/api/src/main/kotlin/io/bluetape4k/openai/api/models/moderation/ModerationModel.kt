package io.bluetape4k.openai.api.models.moderation

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
