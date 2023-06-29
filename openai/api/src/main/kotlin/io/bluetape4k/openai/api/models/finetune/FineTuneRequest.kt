package io.bluetape4k.openai.api.models.finetune

import io.bluetape4k.openai.api.annotations.OpenAIDsl
import io.bluetape4k.openai.api.models.ModelBuilder
import io.bluetape4k.openai.api.models.file.FileId
import io.bluetape4k.openai.api.models.model.ModelId
import java.io.Serializable

data class FineTuneRequest(
    val trainingFile: FileId,
    val validationFile: FileId? = null,
    val model: ModelId? = null,
    val nEpochs: Int? = null,
    val batchSize: Int? = null,
    val learningRateMultiplier: Double? = null,
    val promptLossWeight: Double? = null,
    val computeClassificationMetrics: Boolean? = null,
    val classificationPositiveClass: String? = null,
    val classificationBetas: List<Double>? = null,
    val suffix: String? = null,
): Serializable

@OpenAIDsl
class FineTuneRequestBuilder: ModelBuilder<FineTuneRequest> {

    override fun build(): FineTuneRequest {
        TODO("Not yet implemented")
    }
}


inline fun fineTuneRequest(initializer: FineTuneRequestBuilder.() -> Unit): FineTuneRequest {
    return FineTuneRequestBuilder().apply(initializer).build()
}
