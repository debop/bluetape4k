package io.bluetape4k.openai.api.models.finetune

import com.fasterxml.jackson.annotation.JsonProperty
import io.bluetape4k.openai.api.models.ObjectId
import io.bluetape4k.openai.api.models.Status
import io.bluetape4k.openai.api.models.file.File
import io.bluetape4k.openai.api.models.model.ModelId
import java.io.Serializable

data class FineTuneResult(
    val id: FineTuneId,
    @get:JsonProperty("object")
    val objectId: ObjectId? = null,
    val model: ModelId,
    val createdAt: Long,
    val events: List<FineTuneEvent>? = null,
    val fineTunedModel: ModelId? = null,
    val hyperparams: HyperParameters? = null,
    val organizationId: String? = null,
    val resultFiles: List<File>? = null,
    val status: Status? = null,
    val validationFiles: List<File>? = null,
    val trainingFiles: List<File>? = null,
    val updatedAt: Long? = null,
): Serializable 
