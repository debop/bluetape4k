package io.bluetape4k.openai.client.model.finetune

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

/**
 * Fine tune response
 *
 * 참고: [Fine-tunes API](https://platform.openai.com/docs/api-reference/fine-tunes)
 *
 * @property id
 * @property objectType
 * @property model
 * @property createdAt
 * @property finishedAt
 * @property fineTunedModel
 * @property organizationId
 * @property resultFiles
 * @property status
 * @property validationFile
 * @property trainingFile
 * @property hyperparameters
 * @property trainedTokens
 */
data class FineTuneResponse(
    @JsonProperty("id") val id: String? = null,
    @JsonProperty("object") val objectType: String? = null,
    @JsonProperty("model") val model: String? = null,
    @JsonProperty("created_at") val createdAt: Long? = null,
    @JsonProperty("finished_at") val finishedAt: Long? = null,
    @JsonProperty("fine_tuned_model") val fineTunedModel: String? = null,
    @JsonProperty("organization_id") val organizationId: String? = null,
    @JsonProperty("result_files") val resultFiles: List<String> = emptyList(),
    @JsonProperty("status") val status: String? = null,
    @JsonProperty("validation_file") val validationFile: String? = null,
    @JsonProperty("training_file") val trainingFile: String? = null,
    @JsonProperty("hyperparameters") val hyperparameters: Hyperparameters? = null,
    @JsonProperty("trained_tokens") val trainedTokens: Long? = null,
): Serializable
