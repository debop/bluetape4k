package io.bluetape4k.openai.api.models.finetune

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

/**
 * Fine-tuning job hyperparameters
 *
 * 참고: [fine-tunes](https://beta.openai.com/docs/api-reference/fine-tunes)
 */
data class HyperParameters(
    @get:JsonProperty("batchSize")
    val batchSize: Int? = null,

    @get:JsonProperty("learning_rate_multiplier")
    val learningRateMultiplier: Double? = null,

    // FIXME: nEpochs -> n_epochs 로 naming 변환하는데 문제가 있다. 그래서 강제로 지정해주었다
    @get:JsonProperty("n_epochs")
    val nEpochs: Long? = null,

    @get:JsonProperty("prompt_loss_weight")
    val promptLossWeight: Double? = null,
): Serializable
