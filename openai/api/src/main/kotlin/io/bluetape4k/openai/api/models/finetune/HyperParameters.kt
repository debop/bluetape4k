package io.bluetape4k.openai.api.models.finetune

import java.io.Serializable

/**
 * Fine-tuning job hyperparameters
 *
 * 참고: [fine-tunes](https://beta.openai.com/docs/api-reference/fine-tunes)
 */
data class HyperParameters(
    val batchSize: Int? = null,
    val learningRateMultiplier: Double? = null,
    val nEpochs: Long,
    val promptLossWeight: Double,
) : Serializable
