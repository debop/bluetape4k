package io.bluetape4k.openai.client.model.finetune

import com.fasterxml.jackson.annotation.JsonProperty

data class FineTuneRequest(
    @JsonProperty("model") val model: String,
    @JsonProperty("training_file") val trainingFile: String,
    @JsonProperty("validation_file") val validationFile: String? = null,
    @JsonProperty("hyperparameters") val hyperparameters: Hyperparameters? = null,
    @JsonProperty("suffix") val suffix: String? = null,
)
